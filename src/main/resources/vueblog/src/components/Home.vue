<template>
  <el-container class="home_container">
    <el-header>
      <div class="home_title">V部落博客管理平台</div>
      <div class="home_userinfoContainer">
        <el-dropdown @command="handleCommand">
  <span class="el-dropdown-link home_userinfo" >
      <span style="position: relative;top: 10px;"><img :src="currentUserInfo.userface" :alt="currentUserInfo.nickname" style="width: 30px;height: 30px;border-radius: 50%;"></span>
      <span style="margin: auto;">{{currentUserName}}<i class="el-icon-arrow-down el-icon--right home_userinfo"></i></span>
  </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="sysMsg">系统消息</el-dropdown-item>
            <el-dropdown-item command="MyArticle">我的文章</el-dropdown-item>
            <el-dropdown-item command="MyHome">个人主页</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <!-- <el-button @click="dialogFormVisible = true">ok</el-button> -->
      <el-dialog style="width: 820px; margin: auto;" title="个人主页" :visible.sync="dialogFormVisible">
      <el-card style="width:330px;margin-top: 10px;margin: auto;">
        <div slot="header" style="text-align: left">
          <span>{{currentUserInfo.nickname}}</span>
        </div>
        <div>
          <div><img :src="currentUserInfo.userface" :alt="currentUserInfo.nickname" style="width: 70px;height: 70px"></div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>用户名:</span><span>{{currentUserInfo.username}}</span>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>电子邮箱:</span><span>{{currentUserInfo.email}}</span>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>注册时间:</span><span>{{currentUserInfo.regTime | formatDateTime}}</span>
          </div>
          <div
            style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px;display: flex;align-items: center">
            <span>用户状态:</span>
            <el-switch
              v-model="currentUserInfo.enabled"
              active-text="启用"
              active-color="#13ce66"
              @change="enabledChange(currentUserInfo.enabled,currentUserInfo.id,index)"
              inactive-text="禁用" style="font-size: 12px" disabled="disabled">
            </el-switch>
          </div>
          <div
            style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px;display: flex;align-items: center">
            <span>评论禁言:</span>
            <el-switch
              v-model="currentUserInfo.talk"
              active-text="发言"
              active-color="#13ce66"
              @change="talkChange(currentUserInfo.talk,currentUserInfo.id,index)"
              inactive-text="禁言" style="font-size: 12px" disabled="disabled">
            </el-switch>
          </div>
          <div style="text-align: left;color:#20a0ff;font-size: 12px;margin-top: 13px">
            <span>用户角色:</span>
            <el-tag
              v-for="role in currentUserInfo.roles"
              :key="role.id"
              size="mini"
              style="margin-right: 8px"
              type="success">
              {{role.name}}
            </el-tag>
          </div>
        </div>
      </el-card>
      </el-dialog>
      <el-aside width="200px">
        <el-menu
          default-active="0"
          class="el-menu-vertical-demo" style="background-color: #ECECEC" router>
          <template v-for="(item,index) in this.$router.options.routes" v-if="!item.hidden">
            <el-submenu :index="index+''" v-if="item.children.length>1" :key="index">
              <template slot="title">
                <i :class="item.iconCls"></i>
                <span>{{item.name}}</span>
              </template>
              <el-menu-item v-for="child in item.children" v-if="!child.hidden" :index="child.path" :key="child.path">
                {{child.name}}
              </el-menu-item>
            </el-submenu>
            <template v-else>
              <el-menu-item :index="item.children[0].path">
                <i :class="item.children[0].iconCls"></i>
                <span slot="title">{{item.children[0].name}}</span>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </el-aside>
      <el-container>
        <el-main>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-text="this.$router.currentRoute.name"></el-breadcrumb-item>
          </el-breadcrumb>
          <keep-alive>
            <router-view v-if="this.$route.meta.keepAlive"></router-view>
          </keep-alive>
          <router-view v-if="!this.$route.meta.keepAlive"></router-view>
          <div class="block" v-show="this.$router.currentRoute.name==''">
            <el-timeline style="float: left;text-align: left;width: 100%;">
              <el-timeline-item :timestamp="article.publishDate | formatDate" placement="top" type="primary" v-for="article in articles" :key="article.id">
                <el-card>
                  <h4>
                  <span style="color: #409eff;cursor: pointer" @click="itemClick(article.id)">
                  {{article.title}}
                  </span>
                  <span style="font-size: 5px;">[{{article.cateName}}]</span>
                  </h4>
                  <p>
                      <img :src="article.userface" :alt="article.nickname" width="30" height="30" style="border-radius: 50%;position: relative;top: 10px;">
                      {{article.nickname}} 发表于{{article.publishDate | formatDateTime}}
                  </p>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <el-button @click="loadArticle" v-loading.fullscreen.lock="fullscreenLoading">加载数据</el-button>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>
<script>
  import {getRequest} from '../utils/api'
  export default{
    methods: {
      itemClick(id){
        this.$router.push({path: '/blogDetail', query: {aid: id}})
      },
      loadArticle(){
        var _this = this;
        var count = _this.pagecount+3;
        _this.fullscreenLoading = true;
        if(_this.articles.length < _this.pagecount){
          _this.$alert("数据已全部加载完成");
          _this.fullscreenLoading = false;
        }
        getRequest("/article/getArticleForFirstPage"+"?count="+count).then(function(resp){
          setTimeout(() => {
            _this.fullscreenLoading = false;
          }, 1000);
          _this.articles = resp.data.articles;
          _this.pagecount = resp.data.count;
        });


      },
      handleCommand(command){
        var _this = this;
        if (command == 'logout') {
          this.$confirm('注销登录吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(function () {
            getRequest("/logout")
            _this.currentUserName = '游客';
            _this.$router.replace({path: '/'});
          }, function () {
            //取消
          })
        }else if(command == 'MyHome'){
          this.dialogFormVisible = true;
        }else if(command == 'sysMsg'){
          this.$alert('为了确保所有的小伙伴都能看到完整的数据演示，数据库只开放了查询权限和部分字段的更新权限，其他权限都不具备，完整权限的演示需要大家在自己本地部署后，换一个正常的数据库用户后即可查看，这点请大家悉知!', '友情提示', {
            confirmButtonText: '确定',
            callback: action => {
            }
          });
        }else if(command == 'MyArticle'){
          this.$router.replace({path:'/articleList'})
        }
      }
    },
    mounted: function () {
      this.$alert('为了确保所有的小伙伴都能看到完整的数据演示，数据库只开放了查询权限和部分字段的更新权限，其他权限都不具备，完整权限的演示需要大家在自己本地部署后，换一个正常的数据库用户后即可查看，这点请大家悉知!', '友情提示', {
        confirmButtonText: '确定',
        callback: action => {
        }
      });
      var _this = this;
      _this.loading = false;
      getRequest("/currentUserName").then(function (msg) {
        _this.currentUserName = msg.data;
      }, function (msg) {
        _this.currentUserName = '游客';
      });
      getRequest("/currentUserInfo").then(function(user){
        _this.currentUserInfo = user.data;
      });
      getRequest("/article/getArticleForFirstPage"+"?count="+_this.count).then(function(resp){
        _this.articles = resp.data.articles;
        _this.pagecount = resp.data.count;
      });
    },
    data(){
      return {
        currentUserName: '',
        dialogFormVisible: false,
        currentUserInfo: [],
        articles:[],
        count: '',
        pagecount: '',
        fullscreenLoading: false
      }
    }
  }
</script>
<style>
  .home_container {
    height: 100%;
    position: absolute;
    top: 0px;
    left: 0px;
    width: 100%;
  }

  .el-header {
    background-color: #20a0ff;
    color: #333;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .el-aside {
    background-color: #ECECEC;
  }

  .el-main {
    background-color: #fff;
    color: #000;
    text-align: center;
  }

  .home_title {
    color: #fff;
    font-size: 22px;
    display: inline;
  }

  .home_userinfo {
    color: #fff;
    cursor: pointer;
  }

  .home_userinfoContainer {
    display: inline;
    margin-right: 20px;
  }
</style>
