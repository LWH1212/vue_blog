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
          <el-card class="box-card" style="height: 80px;">
            <div class="text item" style="display: inline-block;">
              <span><img :src="comment.userface" :alt="comment.nickname" style="width: 50px;height: 50px;border-radius: 50%;"></span>
              <div style="display: inline-block;margin-left: 10px;position: relative;bottom: 10px;">
                <span>{{comment.publishDate | formatDateTime}}</span><br>
                <span>{{comment.nickname}}</span>:
                <span style="font-size: 20px;">{{comment.content}}</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-collapse-item>
    </el-collapse>
    </el-col>
    <el-col style="margin-top: 15px;">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item prop="pinglun" label-width="0">
        <el-input type="hidden" v-model="currentUserId" style="display: none;"></el-input>
        <el-input type="textarea" placeholder="请输入评论内容" v-model="ruleForm.pinglun"></el-input>
        </el-form-item>
        <el-button type="primary" style="float: right;" @click="submitForm('ruleForm')">发表评论</el-button>
      </el-form>
    </el-col>
  </el-row>
</template>
<script>
  import {getRequest} from '../utils/api'
  import {postRequest} from '../utils/api'
  export default{
    inject:["reload"],
    methods: {
      goBack(){
        this.$router.go(-1);
      },
      // handleChange(val) {
      //         console.log(val);
      // },
      submitForm(formName) {
              this.$refs[formName].validate((valid) => {
                if (valid) {
                  var _this = this;
                  this.loading = true;
                  this.activeNames = "1";
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
                          _this.$message({type: 'success', message: json.msg});
                          this.$refs[formName].resetFields();
                          getRequest("/getComments/" + aid).then(resp=> {
                            if (resp.status == 200) {
                              _this.comments = resp.data;
                            }
                            _this.loading = false;
                          }, resp=> {
                            _this.loading = false;
                            _this.$message({type: 'error', message: '页面加载失败!'});
                          });
                        } else if(json.status == 'error' ){
                          _this.$message(json.msg);
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
      resetForm(formName) {
        this.$refs[formName].resetFields();
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
      getRequest("/getComments/" + aid).then(resp=> {
        if (resp.status == 200) {
          _this.comments = resp.data;
        }
        _this.loading = false;
      }, resp=> {
        _this.loading = false;
        _this.$message({type: 'error', message: '页面加载失败!'});
      });
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
