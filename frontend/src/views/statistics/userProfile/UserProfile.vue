
<template>
  <div class="page-container" style="width: 100%">
    <!-- 年龄组分析 -->
    <a-row :gutter="16" class="age-group-row">
      <a-col :span="24">
        <a-card title="📊 年龄组分析" :bordered="false" class="age-group-card">
          <div class="age-group-list">
            <div
              v-for="(group, index) in userProfile.ageGroupAnalysis"
              :key="index"
              class="age-group-item"
            >
              <div class="age-group-header">
                <div class="age-group-title">
                  <a-icon type="team" theme="filled" style="color: #1890ff; margin-right: 8px;" />
                  {{ group.ageGroupName }}
                </div>
                <a-tag color="blue" size="large">{{ group.totalUsers }} 人</a-tag>
              </div>

              <div class="age-group-content">
                <a-row :gutter="16" class="stats-grid">
                  <a-col :span="8">
                    <div class="stat-box">
                      <div class="stat-label">总提问数</div>
                      <div class="stat-value primary">{{ group.totalQuestions }}</div>
                      <div class="stat-detail">人均 {{ group.avgQuestionsPerUser }} 次</div>
                    </div>
                  </a-col>
                  <a-col :span="8">
                    <div class="stat-box">
                      <div class="stat-label">活跃度</div>
                      <div class="stat-value warning">{{ group.activeRate }}%</div>
                      <a-progress
                        :percent="group.activeRate"
                        strokeColor="#faad14"
                        size="small"
                        :show-info="false"
                      />
                    </div>
                  </a-col>
                  <a-col :span="8">
                    <div class="stat-box">
                      <div class="stat-label">偏好时段</div>
                      <div class="stat-value info">{{ group.preferredTimeSlot }}</div>
                      <div class="stat-detail">学习黄金时间</div>
                    </div>
                  </a-col>
                </a-row>

                <div class="focus-areas">
                  <div class="focus-title">
                    <a-icon type="star" theme="filled" style="color: #faad14; margin-right: 8px;" />
                    关注 TOP3
                  </div>
                  <div class="focus-list">
                    <div
                      v-for="(topic, tIndex) in group.topFocusAreas"
                      :key="tIndex"
                      class="focus-item"
                    >
                      <a-badge :count="tIndex + 1" :numberStyle="{ backgroundColor: getRankColor(tIndex) }" />
                      <span class="focus-text">{{ topic }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <a-empty
              v-if="!userProfile.ageGroupAnalysis || userProfile.ageGroupAnalysis.length === 0"
              description="暂无年龄组数据"
              :image="simpleImage"
            />
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 关注点对比 -->
    <a-row :gutter="16" class="focus-row">
      <a-col :span="24">
        <a-card title="🎯 不同年龄段关注点对比" :bordered="false" class="focus-card">
          <a-tabs default-active-key="young" class="focus-tabs">
            <a-tab-pane key="young" tab="青年组 (35 岁以下)">
              <div class="focus-comparison">
                <div
                  v-for="(item, index) in userProfile.focusComparison.youngGroupFocus"
                  :key="index"
                  class="focus-rank-item"
                >
                  <div class="rank-number" :class="'rank-' + (index + 1)">
                    {{ index + 1 }}
                  </div>
                  <div class="focus-detail">
                    <div class="focus-keyword">{{ item.keyword }}</div>
                    <div class="focus-stats">
                      <a-progress
                        :percent="item.percentage * 4"
                        :strokeColor="getProgressColor(index)"
                        :show-info="false"
                        size="default"
                      />
                      <div class="stat-tags">
                        <a-tag color="blue">{{ item.mentionCount }} 次提及</a-tag>
                        <a-tag color="green">{{ item.percentage }}% 占比</a-tag>
                        <a-tag :color="item.trend === 0 ? 'default' : 'red'">
                          <a-icon :type="item.trend === 0 ? 'minus' : 'rise'" />
                          {{ item.trend === 0 ? '平稳' : '上升' }}
                        </a-tag>
                      </div>
                    </div>
                  </div>
                </div>

                <a-empty
                  v-if="!userProfile.focusComparison.youngGroupFocus || userProfile.focusComparison.youngGroupFocus.length === 0"
                  description="暂无青年组关注数据"
                  :image="simpleImage"
                />
              </div>
            </a-tab-pane>

            <a-tab-pane key="middle" tab="中年组 (35-50 岁)">
              <a-empty
                v-if="!userProfile.focusComparison.middleGroupFocus || userProfile.focusComparison.middleGroupFocus.length === 0"
                description="暂无中年组关注数据"
                :image="simpleImage"
              />
            </a-tab-pane>

            <a-tab-pane key="senior" tab="老年组 (50 岁以上)">
              <a-empty
                v-if="!userProfile.focusComparison.seniorGroupFocus || userProfile.focusComparison.seniorGroupFocus.length === 0"
                description="暂无老年组关注数据"
                :image="simpleImage"
              />
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <!-- 群体特征标签 -->
    <a-row :gutter="16" class="feature-row">
      <a-col :span="24">
        <a-card title="🏷️ 群体特征标签" :bordered="false" class="feature-card">
          <div class="feature-list">
            <div
              v-for="(group, index) in userProfile.groupFeatureTags"
              :key="index"
              class="feature-item"
            >
              <div class="feature-header">
                <div class="feature-title">
                  <a-avatar :style="{ backgroundColor: getAvatarColor(index) }">
                    <a-icon type="user" />
                  </a-avatar>
                  {{ group.groupName }}
                </div>
              </div>

              <div class="feature-content">
                <div class="feature-tags">
                  <a-tag
                    v-for="(tag, tIndex) in group.featureTags"
                    :key="tIndex"
                    color="processing"
                    class="feature-tag"
                  >
                    <a-icon type="check-circle" theme="filled" />
                    {{ tag }}
                  </a-tag>
                </div>

                <div class="feature-description">
                  <a-icon type="file-text" theme="filled" style="color: #1890ff; margin-right: 8px;" />
                  {{ group.groupDescription }}
                </div>
              </div>
            </div>

            <a-empty
              v-if="!userProfile.groupFeatureTags || userProfile.groupFeatureTags.length === 0"
              description="暂无群体特征数据"
              :image="simpleImage"
            />
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>import { Empty } from 'ant-design-vue'

export default {
  name: "UserProfile",
  data () {
    return {
      userProfile: {
        ageGroupAnalysis: [],
        departmentAnalysis: [],
        focusComparison: {
          youngGroupFocus: [],
          middleGroupFocus: [],
          seniorGroupFocus: []
        },
        groupFeatureTags: []
      },
      simpleImage: Empty.PRESENTED_IMAGE_SIMPLE
    }
  },
  mounted () {
    this.queryUserProfile()
  },
  methods: {
    queryUserProfile () {
      this.$get('/business/web/userProfile').then((r) => {
        if (r.data.code === 0 && r.data.data) {
          this.userProfile = r.data.data
        }
      }).catch((r) => {
        console.error(r)
        this.$message.error('获取用户画像数据失败')
      })
    },
    getRankColor (index) {
      const colors = ['#ff4d4f', '#faad14', '#52c41a']
      return colors[index] || '#1890ff'
    },
    getProgressColor (index) {
      const colors = ['#ff4d4f', '#faad14', '#52c41a', '#1890ff']
      return colors[index] || '#722ed1'
    },
    getAvatarColor (index) {
      const colors = ['#1890ff', '#52c41a', '#faad14', '#722ed1', '#13c2c2']
      return colors[index % colors.length]
    }
  }
}
</script>

<style scoped lang="less">.page-container {
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f3f3f3 100%);
  min-height: calc(100vh - 64px);
}

.age-group-row,
.focus-row,
.feature-row {
  margin-bottom: 24px;
}

.age-group-card,
.focus-card,
.feature-card {
  border-radius: 3px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.95);

  /deep/ .ant-card-head {
    background: linear-gradient(135deg, #f0f5ff 0%, #e6f7ff 100%);
    border-bottom: 2px solid #1890ff;
    padding: 16px 20px;

    /deep/ .ant-card-head-title {
      font-size: 16px;
      font-weight: 600;
      color: #003a8c;
    }
  }
}

.age-group-list {
  .age-group-item {
    padding: 20px;
    margin-bottom: 20px;
    background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
    border-radius: 3px;
    border: 2px solid #e8e8e8;
    transition: all 0.3s;

    &:hover {
      border-color: #1890ff;
      background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(24, 144, 255, 0.15);
    }

    .age-group-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 16px;
      border-bottom: 2px dashed #d9d9d9;

      .age-group-title {
        font-size: 18px;
        font-weight: 700;
        color: #333;
        display: flex;
        align-items: center;
      }
    }

    .age-group-content {
      .stats-grid {
        margin-bottom: 20px;

        .stat-box {
          padding: 16px;
          background: white;
          border-radius: 3px;
          border: 2px solid #e8e8e8;
          text-align: center;
          transition: all 0.3s;

          &:hover {
            border-color: #1890ff;
            box-shadow: 0 4px 12px rgba(24, 144, 255, 0.1);
          }

          .stat-label {
            font-size: 13px;
            color: #999;
            margin-bottom: 8px;
          }

          .stat-value {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 8px;

            &.primary {
              color: #1890ff;
            }

            &.success {
              color: #52c41a;
            }

            &.warning {
              color: #faad14;
            }

            &.info {
              color: #722ed1;
            }
          }

          .stat-detail {
            font-size: 12px;
            color: #666;
          }
        }
      }

      .focus-areas {
        padding: 16px;
        background: white;
        border-radius: 3px;
        border: 2px solid #e8e8e8;

        .focus-title {
          font-size: 15px;
          font-weight: 600;
          color: #333;
          margin-bottom: 12px;
          display: flex;
          align-items: center;
        }

        .focus-list {
          .focus-item {
            display: flex;
            align-items: center;
            padding: 10px;
            margin-bottom: 8px;
            background: #fafafa;
            border-radius: 3px;
            transition: all 0.3s;

            &:hover {
              background: #f0f5ff;
              transform: translateX(4px);
            }

            .focus-text {
              flex: 1;
              font-size: 14px;
              color: #333;
              line-height: 1.6;
            }
          }
        }
      }
    }
  }
}

.focus-card {
  .focus-tabs {
    /deep/ .ant-tabs-nav {
      background: #fafafa;
      border-radius: 3px 3px 0 0;
      padding: 0 16px;

      /deep/ .ant-tabs-tab {
        font-size: 14px;
        font-weight: 600;
        padding: 12px 24px;
      }
    }
  }

  .focus-comparison {
    padding: 20px;

    .focus-rank-item {
      display: flex;
      align-items: flex-start;
      padding: 16px;
      margin-bottom: 16px;
      background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
      border-radius: 3px;
      border-left: 4px solid #1890ff;
      transition: all 0.3s;

      &:hover {
        transform: translateX(8px);
        box-shadow: 0 4px 16px rgba(24, 144, 255, 0.15);
      }

      .rank-number {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        font-weight: 700;
        color: white;
        margin-right: 16px;
        flex-shrink: 0;

        &.rank-1 {
          background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
          box-shadow: 0 2px 8px rgba(255, 77, 79, 0.4);
        }

        &.rank-2 {
          background: linear-gradient(135deg, #faad14 0%, #d48806 100%);
          box-shadow: 0 2px 8px rgba(250, 173, 20, 0.4);
        }

        &.rank-3 {
          background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
          box-shadow: 0 2px 8px rgba(82, 196, 26, 0.4);
        }
      }

      .focus-detail {
        flex: 1;

        .focus-keyword {
          font-size: 15px;
          font-weight: 600;
          color: #333;
          margin-bottom: 12px;
          line-height: 1.5;
        }

        .focus-stats {
          .stat-tags {
            margin-top: 12px;
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
          }
        }
      }
    }
  }
}

.feature-card {
  .feature-list {
    .feature-item {
      padding: 20px;
      margin-bottom: 20px;
      background: linear-gradient(135deg, #ffffff 0%, #f6f6f6 100%);
      border-radius: 3px;
      border: 2px solid #f1f1f1;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(82, 196, 26, 0.2);
      }

      .feature-header {
        margin-bottom: 16px;

        .feature-title {
          font-size: 18px;
          font-weight: 700;
          color: #333;
          display: flex;
          align-items: center;
          gap: 12px;
        }
      }

      .feature-content {
        .feature-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          margin-bottom: 16px;

          .feature-tag {
            font-size: 13px;
            padding: 4px 12px;
            border-radius: 3px;
            background: rgba(24, 144, 255, 0.1);
            border: 1px solid #1890ff;
            color: #1f1f1f;

            i {
              margin-right: 4px;
            }
          }
        }

        .feature-description {
          padding: 16px;
          background: white;
          border-radius: 3px;
          border-left: 4px solid #1890ff;
          font-size: 14px;
          line-height: 1.8;
          color: #666;
        }
      }
    }
  }
}
</style>
