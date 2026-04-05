<template>
  <a-card :bordered="false" class="card-area">
    <div :class="advanced ? 'search' : null">
      <!-- 搜索区域 -->
      <a-form layout="horizontal">
        <a-row :gutter="15">
          <div :class="advanced ? null: 'fold'">
            <a-col :md="6" :sm="24">
              <a-form-item
                label="标题"
                :labelCol="{span: 5}"
                :wrapperCol="{span: 18, offset: 1}">
                <a-input v-model="queryParams.title"/>
              </a-form-item>
            </a-col>
          </div>
          <span style="float: right; margin-top: 3px;">
            <a-button type="primary" @click="search">查询</a-button>
            <a-button style="margin-left: 8px" @click="reset">重置</a-button>
          </span>
        </a-row>
      </a-form>
    </div>
    <div>
      <div class="operator">
<!--        <a-button type="primary" ghost @click="add">新增</a-button>-->
        <a-button type="primary" ghost @click="showImportModal" style="margin-left: 8px">导入</a-button>
        <a-button @click="batchDelete">删除</a-button>
<!--        <a-button @click="batchDelete1">删除</a-button>-->
      </div>
      <!-- 表格区域 -->
      <a-table ref="TableInfo"
               :columns="columns"
               :rowKey="record => record.id"
               :dataSource="dataSource"
               :pagination="pagination"
               :loading="loading"
               :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
               :scroll="{ x: 900 }"
               @change="handleTableChange">
        <template slot="titleShow" slot-scope="text, record">
          <template>
            <a-badge status="processing" v-if="record.rackUp === 1"/>
            <a-badge status="error" v-if="record.rackUp === 0"/>
            <a-tooltip>
              <template slot="title">
                {{ record.title }}
              </template>
              {{ record.title.slice(0, 8) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="contentShow" slot-scope="text, record">
          <template>
            <a-tooltip>
              <template slot="title">
                {{ record.content }}
              </template>
              {{ record.content.slice(0, 30) }} ...
            </a-tooltip>
          </template>
        </template>
        <template slot="operation" slot-scope="text, record">
          <a-icon type="setting" theme="twoTone" twoToneColor="#4a9ff5" @click="edit(record)" title="修 改"></a-icon>
        </template>
      </a-table>
    </div>
    <a-modal
      title="导入JSON文件"
      :visible="importModalVisible"
      :confirmLoading="importLoading"
      @ok="handleImportOk"
      @cancel="handleImportCancel"
      okText="导入"
      cancelText="取消">
      <a-upload
        name="file"
        :multiple="false"
        :beforeUpload="beforeUpload"
        :fileList="fileList"
        @remove="handleRemove"
        accept=".json">
        <a-button>
          <a-icon type="upload" />
          选择JSON文件
        </a-button>
      </a-upload>
      <div style="margin-top: 10px; color: #999;">
        请上传JSON格式的文件
      </div>
    </a-modal>
    <a-modal
      title="编辑课程内容"
      :visible="editModalVisible"
      :confirmLoading="editLoading"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
      okText="保存"
      cancelText="取消"
      width="900px">
      <a-form-model
        ref="editForm"
        :model="editForm"
        :rules="editRules"
        :label-col="{ span: 24 }"
        :wrapper-col="{ span: 24 }"
        class="lesson-edit-form">
        <a-divider orientation="left">
          <a-icon type="book" style="margin-right: 5px" />
          标题信息
        </a-divider>

        <a-form-model-item label="英文标题" prop="title">
          <a-input
            v-model="editForm.title"
            placeholder="请输入英文标题">
            <a-icon slot="prefix" type="font-colors" />
          </a-input>
        </a-form-model-item>

        <a-form-model-item label="中文标题" prop="titleTranslate">
          <a-input
            v-model="editForm.titleTranslate"
            placeholder="请输入中文标题翻译">
            <a-icon slot="prefix" type="translation" />
          </a-input>
        </a-form-model-item>

        <a-divider orientation="left">
          <a-icon type="file-text" style="margin-right: 5px" />
          文章内容
        </a-divider>

        <a-form-model-item label="英文正文" prop="text">
          <a-textarea
            v-model="editForm.text"
            placeholder="请输入英文正文内容，支持换行"
            :rows="8"
            class="content-textarea" />
        </a-form-model-item>

        <a-form-model-item label="中文翻译" prop="textTranslate">
          <a-textarea
            v-model="editForm.textTranslate"
            placeholder="请输入中文翻译内容，支持换行"
            :rows="8"
            class="content-textarea" />
        </a-form-model-item>

<!--        <a-form-model-item label="生词列表" prop="newWords">-->
<!--          <a-textarea-->
<!--            v-model="editForm.newWords"-->
<!--            placeholder='请输入JSON格式，例如：[{"word": "theatre", "trans": "剧院"}]'-->
<!--            :rows="3"-->
<!--            class="json-textarea" />-->
<!--          <div class="format-tip">-->
<!--            <a-icon type="info-circle" />-->
<!--            格式：[{"word": "单词", "trans": "释义"}]-->
<!--          </div>-->
<!--        </a-form-model-item>-->
      </a-form-model>
    </a-modal>
  </a-card>
</template>

<script>
import RangeDate from '@/components/datetime/RangeDate'
import {mapState} from 'vuex'
import moment from 'moment'
moment.locale('zh-cn')

export default {
  name: 'Bulletin',
  components: {RangeDate},
  data () {
    return {
      advanced: false,
      bulletinAdd: {
        visiable: false
      },
      bulletinEdit: {
        visiable: false
      },
      importModalVisible: false,
      importLoading: false,
      fileList: [],
      editModalVisible: false,
      editLoading: false,
      editForm: {
        id: null,
        title: '',
        titleTranslate: '',
        text: '',
        textTranslate: '',
        newWords: ''
      },
      editRules: {
        title: [
          { required: true, message: '请输入英文标题', trigger: 'blur' }
        ],
        text: [
          { required: true, message: '请输入英文正文', trigger: 'blur' }
        ]
      },
      queryParams: {},
      filteredInfo: null,
      sortedInfo: null,
      paginationInfo: null,
      dataSource: [],
      selectedRowKeys: [],
      loading: false,
      pagination: {
        pageSizeOptions: ['10', '20', '30', '40', '100'],
        defaultCurrent: 1,
        defaultPageSize: 10,
        showQuickJumper: true,
        showSizeChanger: true,
        showTotal: (total, range) => `显示 ${range[0]} ~ ${range[1]} 条记录，共 ${total} 条记录`
      },
      userList: []
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.account.user
    }),
    columns () {
      return [{
        title: '标题',
        dataIndex: 'title',
        ellipsis: true,
        width: 200
      }, {
        title: '标题翻译',
        dataIndex: 'titleTranslate',
        customRender: (text, row, index) => {
          if (text) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true,
        width: 150
      }, {
        title: '正文内容',
        dataIndex: 'text',
        customRender: (text, row, index) => {
          if (text) {
            const preview = text.substring(0, 50).replace(/\n/g, ' ')
            return (
              <a-tooltip title={text}>
                {preview}...
              </a-tooltip>
            )
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '正文翻译',
        dataIndex: 'textTranslate',
        customRender: (text, row, index) => {
          if (text) {
            const preview = text.substring(0, 50).replace(/\n/g, ' ')
            return (
              <a-tooltip title={text}>
                {preview}...
              </a-tooltip>
            )
          } else {
            return '- -'
          }
        },
        ellipsis: true
      }, {
        title: '创建时间',
        dataIndex: 'createdAt',
        customRender: (text, row, index) => {
          if (text) {
            return text
          } else {
            return '- -'
          }
        },
        ellipsis: true,
        width: 180
      }, {
        title: '操作',
        dataIndex: 'operation',
        scopedSlots: {customRender: 'operation'},
        width: 100
      }]
    }
  },
  mounted () {
    this.fetch()
  },
  methods: {
    edit (record) {
      this.editForm = {
        id: record.id,
        title: record.title || '',
        titleTranslate: record.titleTranslate || '',
        text: record.text || '',
        textTranslate: record.textTranslate || '',
        newWords: record.newWords || ''
      }
      this.editModalVisible = true
    },
    handleEditCancel () {
      this.editModalVisible = false
      this.$refs.editForm.resetFields()
    },
    handleEditOk () {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.editLoading = true
          this.$put('/business/lesson-content', this.editForm).then(() => {
            this.$message.success('修改成功')
            this.editModalVisible = false
            this.editLoading = false
            this.search()
          }).catch(() => {
            this.editLoading = false
          })
        }
      })
    },
    showImportModal () {
      this.importModalVisible = true
      this.fileList = []
    },
    handleImportCancel () {
      this.importModalVisible = false
      this.fileList = []
    },
    beforeUpload (file) {
      const isJson = file.type === 'application/json' || file.name.endsWith('.json')
      if (!isJson) {
        this.$message.error('只能上传JSON文件!')
        return false
      }
      this.fileList = [file]
      return false
    },
    handleRemove () {
      this.fileList = []
    },
    handleImportOk () {
      if (this.fileList.length === 0) {
        this.$message.warning('请先选择要导入的文件')
        return
      }

      const file = this.fileList[0]
      const formData = new FormData()
      formData.append('file', file)

      this.importLoading = true

      this.$upload('/business/lesson-content/import', formData).then((r) => {
        this.$message.success('导入成功')
        this.importModalVisible = false
        this.fileList = []
        this.importLoading = false
        this.search()
      }).catch(() => {
        this.importLoading = false
      })
    },
    onSelectChange (selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    toggleAdvanced () {
      this.advanced = !this.advanced
    },
    add () {
      this.bulletinAdd.visiable = true
    },
    handleBulletinAddClose () {
      this.bulletinAdd.visiable = false
    },
    handleBulletinAddSuccess () {
      this.bulletinAdd.visiable = false
      this.$message.success('新增公告成功')
      this.search()
    },
    handleBulletinEditClose () {
      this.bulletinEdit.visiable = false
    },
    handleBulletinEditSuccess () {
      this.bulletinEdit.visiable = false
      this.$message.success('修改公告成功')
      this.search()
    },
    handleDeptChange (value) {
      this.queryParams.deptId = value || ''
    },
    batchDelete1 () {
      this.$get('/business/supplier-info/batchEditSupplierName').then((r) => {
      })
    },
    batchDelete () {
      if (!this.selectedRowKeys.length) {
        this.$message.warning('请选择需要删除的记录')
        return
      }
      let that = this
      this.$confirm({
        title: '确定删除所选中的记录?',
        content: '当您点击确定按钮后，这些记录将会被彻底删除',
        centered: true,
        onOk () {
          let ids = that.selectedRowKeys.join(',')
          that.$delete('/business/lesson-content/' + ids).then(() => {
            that.$message.success('删除成功')
            that.selectedRowKeys = []
            that.search()
          })
        },
        onCancel () {
          that.selectedRowKeys = []
        }
      })
    },
    search () {
      let {sortedInfo, filteredInfo} = this
      let sortField, sortOrder
      // 获取当前列的排序和列的过滤规则
      if (sortedInfo) {
        sortField = sortedInfo.field
        sortOrder = sortedInfo.order
      }
      this.fetch({
        sortField: sortField,
        sortOrder: sortOrder,
        ...this.queryParams,
        ...filteredInfo
      })
    },
    reset () {
      // 取消选中
      this.selectedRowKeys = []
      // 重置分页
      this.$refs.TableInfo.pagination.current = this.pagination.defaultCurrent
      if (this.paginationInfo) {
        this.paginationInfo.current = this.pagination.defaultCurrent
        this.paginationInfo.pageSize = this.pagination.defaultPageSize
      }
      // 重置列过滤器规则
      this.filteredInfo = null
      // 重置列排序规则
      this.sortedInfo = null
      // 重置查询参数
      this.queryParams = {}
      this.fetch()
    },
    handleTableChange (pagination, filters, sorter) {
      // 将这三个参数赋值给Vue data，用于后续使用
      this.paginationInfo = pagination
      this.filteredInfo = filters
      this.sortedInfo = sorter

      this.fetch({
        sortField: sorter.field,
        sortOrder: sorter.order,
        ...this.queryParams,
        ...filters
      })
    },
    fetch (params = {}) {
      // 显示loading
      this.loading = true
      if (this.paginationInfo) {
        // 如果分页信息不为空，则设置表格当前第几页，每页条数，并设置查询分页参数
        this.$refs.TableInfo.pagination.current = this.paginationInfo.current
        this.$refs.TableInfo.pagination.pageSize = this.paginationInfo.pageSize
        params.size = this.paginationInfo.pageSize
        params.current = this.paginationInfo.current
      } else {
        // 如果分页信息为空，则设置为默认值
        params.size = this.pagination.defaultPageSize
        params.current = this.pagination.defaultCurrent
      }
      this.$get('/business/lesson-content/page', {
        ...params
      }).then((r) => {
        let data = r.data.data
        const pagination = {...this.pagination}
        pagination.total = data.total
        this.dataSource = data.records
        this.pagination = pagination
        // 数据加载完毕，关闭loading
        this.loading = false
      })
    }
  },
  watch: {}
}
</script>
<style lang="less" scoped>@import "../../../../static/less/Common";

.lesson-edit-form {
  max-height: 600px;
  overflow-y: auto;
  padding-right: 10px;

  /deep/ .ant-divider-inner-text {
    font-size: 14px;
    font-weight: 500;
    color: #1890ff;
  }

  /deep/ .ant-form-item {
    margin-bottom: 16px;
  }

  /deep/ .ant-form-item-label > label {
    font-weight: 500;
    color: #262626;
  }

  /deep/ .ant-input,
  /deep/ .ant-textarea {
    border-radius: 4px;
    transition: all 0.3s;

    &:focus {
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }

  /deep/ .ant-input-affix-wrapper {
    border-radius: 4px;
  }

  .content-textarea {
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 13px;
    line-height: 1.8;
    white-space: pre-wrap;
  }

  .json-textarea {
    font-family: 'Consolas', 'Monaco', monospace;
    font-size: 12px;
    line-height: 1.6;
  }

  .format-tip {
    display: flex;
    align-items: center;
    gap: 5px;
    margin-top: 6px;
    padding: 6px 10px;
    background: linear-gradient(135deg, #f0f7ff 0%, #e6f4ff 100%);
    border-left: 3px solid #1890ff;
    border-radius: 4px;
    color: #1890ff;
    font-size: 12px;
    line-height: 1.5;

    i {
      font-size: 14px;
      flex-shrink: 0;
    }
  }
}

/deep/ .ant-modal-body {
  padding: 24px 32px;
  background: #fafafa;
}

/deep/ .ant-modal-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;

  .ant-modal-title {
    color: #fff;
    font-weight: 600;
    font-size: 16px;
  }
}

/deep/ .ant-modal-close {
  color: #fff;

  &:hover {
    color: #f0f0f0;
  }
}

/deep/ .ant-modal-footer {
  border-top: 1px solid #e8e8e8;
  padding: 16px 24px;
  background: #fff;
  border-radius: 0 0 4px 4px;

  .ant-btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);

    &:hover {
      background: linear-gradient(135deg, #5568d3 0%, #65408b 100%);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
  }
}
</style>
