<template>
  <el-row v-loading="loading">
    <el-col :span="24">
      <div style="text-align: left;">
        <el-button type="text" icon="el-icon-back" @click="goBack" style="padding-bottom: 0px;">返回</el-button>
      </div>
    </el-col>
    <el-col :span="24">
      <div>
        <div><h3 style="margin-top: 0px;margin-bottom: 0px">{{article.title}}</h3></div>
        <div style="width: 100%;margin-top: 5px;display: flex;justify-content: flex-end;align-items: center">
          <div style="display: inline; color: #20a0ff;margin-left: 50px;margin-right:20px;font-size: 12px;">
            {{article.nickname}}
          </div>
          <span style="color: #20a0ff;margin-right:20px;font-size: 12px;">浏览 {{article.pageView==null?0:article.pageView}}</span>
          <span style="color: #20a0ff;margin-right:20px;font-size: 12px;"> {{article.editTime | formatDateTime}}</span>
          <el-tag type="success" v-for="(item,index) in article.tags" :key="index" size="small"
                  style="margin-left: 8px">{{item.tagName}}
          </el-tag>
          <span style="margin:0px 50px 0px 0px"></span>
        </div>
      </div>
    </el-col>
    <el-col>
      <div style="text-align: left" v-html="article.htmlContent">
      </div>
    </el-col>
    <el-col>
    <el-collapse style="text-align: left;" v-model="activeNames">
      <el-collapse-item title="评论列表" name="1">
        <div v-for="comment in comments">
          <el-card class="box-card">
            <div class="text item" style="display: inline-block;width: 100%;">
              <div style="display: inline-block;width: 5%;">
                <el-popover
                    placement="top"
                    title="用户信息"
                    width="200"
                    trigger="hover">
                    <span>用户名：{{comment.username}}</span><br>
                    <span v-show="comment.email != null">&nbsp;&nbsp;&nbsp;邮箱：{{comment.email}}</span>
                    <span v-show="comment.email == null">&nbsp;&nbsp;&nbsp;邮箱：暂无</span>
                    <img slot="reference" :src="comment.userface" :alt="comment.nickname" style="width: 50px;height: 50px;border-radius: 50%;">
                  </el-popover>
              </div>
              <div style="display: inline-block;margin-left: 10px;width: 85%;">
                <span>{{comment.publishDate | formatDateTime}}</span><br>
                <span>{{comment.nickname}}</span>:
                <span style="font-size: 20px;">{{comment.content}}</span>
              </div>
              <div style="display: inline-block;width: 5%;">
                <el-button v-show="currentUserId == comment.uid" style="float: right; padding: 3px 0;color: #ff0509" type="text" icon="el-icon-delete"
                           @click="deleteComment(comment.id)">删除
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
        <div v-show="this.comments.length == 0">
          <div style="font-size: 30px; background-color: #909399;text-align: center;margin-top: 10px;" >暂无评论</div>
        </div>
        <el-pagination style="float: right;margin: 10px;"
          background
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="totalCount" @current-change="currentChange" v-show="this.comments.length>0">
        </el-pagination>
      </el-collapse-item>
    </el-collapse>
    </el-col>
    <el-col style="margin-top: 15px;" v-show="article.state==1">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item prop="pinglun" label-width="0">
        <el-input type="hidden" v-model="currentUserId" style="display: none;"></el-input>
        <el-input
          type="textarea"
          placeholder="请输入评论内容"
          v-model.trim="ruleForm.pinglun"
          :maxlength="100"
          show-word-limit
        >
        </el-input>
        </el-form-item>
        <el-button type="primary" style="float: right;" @click="submitForm('ruleForm')">发表评论</el-button>
      </el-form>
    </el-col>
  </el-row>
</template>
<script>
  import {getRequest} from '../utils/api'
  import {postRequest} from '../utils/api'
  import {deleteRequest} from '../utils/api'
  export default{
    inject:["reload"],
    methods: {
      goBack(){
        this.$router.go(-1);
      },
      //翻页
      currentChange(currentPage){
        this.currentPage = currentPage;
        this.loading = true;
        this.loadComments(currentPage, this.pageSize);
      },
      submitForm(formName) {
              this.$refs[formName].validate((valid) => {
                if (valid) {
                  var _this = this;
                  this.loading = true;
                    postRequest('/addComment',
                    {
                      aid: _this.article.id,
                      content: _this.ruleForm.pinglun,
                      uid: _this.currentUserId
                    },
                    ).then(resp=> {
                      _this.loading = false;
                      if (resp.status == 200) {
                        //成功
                        var json = resp.data;
                        var aid = this.$route.query.aid;
                        if (json.status == 'success') {
                          this.activeNames = "1";
                          _this.$message({type: 'success', message: json.msg});
                          this.$refs[formName].resetFields();
                          this.loadComments(1,this.pageSize);
                        } else if(json.status == 'error' ){
                          _this.$message({type:'warning',message:json.msg});
                        }
                      } else {
                        //失败
                        _this.$alert(json.msg);
                      }
                    }, resp=> {
                      _this.loading = false;
                      _this.$alert('找不到服务器⊙﹏⊙∥!', '失败!');
                    });
                } else {
                  console.log('error submit!!');
                  return false;
                }
              });
      },

      deleteComment(id){
        var _this = this;
        this.$confirm('删除该评论, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.loading = true;
          deleteRequest("/deleteComment/" + id).then(resp=> {
            if (resp.status == 200 && resp.data.status == 'success') {
              _this.$message({type: 'success', message: '删除成功!'})
              _this.loadComments(1,this.pageSize);
              return;
            }
            _this.loading = false;
            _this.$message({type: 'error', message: '删除失败!'})
          }, resp=> {
            _this.loading = false;
            _this.$message({type: 'error', message: '删除失败!'})
          });
        }).catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });
      },

      resetForm(formName) {
        this.$refs[formName].resetFields();
      },

      loadComments(page,count){
        var aid = this.$route.query.aid;
        var _this = this;
        getRequest("/getComments" +"?aid="+ aid+"&page="+page+"&count="+count).then(resp=> {
          if (resp.status == 200) {
            _this.comments = resp.data.comments;
            _this.totalCount = resp.data.totalCount;
          }
          _this.loading = false;
        }, resp=> {
          _this.loading = false;
          _this.$message({type: 'error', message: '页面加载失败!'});
        });
      }

    },
    mounted: function () {
      var aid = this.$route.query.aid;
      this.activeName = this.$route.query.an
      var _this = this;
      this.loading = true;
      getRequest("/article/" + aid).then(resp=> {
        if (resp.status == 200) {
          _this.article = resp.data;
        }
        _this.loading = false;
      }, resp=> {
        _this.loading = false;
        _this.$message({type: 'error', message: '页面加载失败!'});
      });
      _this.loadComments(1,this.pageSize);
      getRequest("/currentUserId").then(function(user){
        _this.currentUserId = user.data;
      })
    },
    data(){
      return {
        article: {},
        loading: false,
        activeName: '',
        activeNames: '',
        currentPage: 1,
        totalCount: -1,
        pageSize: 5,
        comments: [],
        currentUserId: '',
        ruleForm:{
          pinglun: ''
        },
        rules: {
          pinglun: [
                      { required: true, message: '请输入评论内容', trigger: 'foucs' }
                    ]
        }
    }

   }
  }
</script>
