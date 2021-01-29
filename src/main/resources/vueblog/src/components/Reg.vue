<template>
  <el-form style="position: relative;bottom: 120px;"  class="login-container" label-position="left"
           label-width="0px">
    <h3 class="login_title">添加用户</h3>
    <el-form-item >

      <el-input id="userface" style="display: none" type="file" v-model="user.userface"></el-input>
      <div src="" id="preview" @click="dakai()">
      </div>
    </el-form-item>
    <el-form-item >
      <el-input type="text" v-model.trim="user.username" auto-complete="off" placeholder="用户名"></el-input>
    </el-form-item>
	<el-form-item>
	  <el-input type="text" v-model.trim="user.nickname" auto-complete="off" placeholder="昵称"></el-input>
	</el-form-item>
    <el-form-item >
      <el-input type="password" v-model.trim="user.password" auto-complete="off" placeholder="密码"></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button type="primary" @click.native.prevent="submitClick" style="width: 100%">添加</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
  import {dakai} from '../utils/upload'
  import {postRequest} from '../utils/api'
  import {uploadFileRequest} from '../utils/api'
  import {putRequest} from '../utils/api'
  import {isNotNullORBlank} from '../utils/utils'
  export default{
    data(){
      return {
        loading: false,
        user: {
          // userface: '',
          username: '',
          nickname: '',
          password: ''
        },
      }
    },
    methods: {

      submitClick: function () {
        if (!(isNotNullORBlank(this.user.userface,this.user.username, this.user.nickname, this.user.password))) {
          this.$message({type: 'error', message: '数据不能为空!'});
          return;
        }
        var _this = this;
        this.loading = true;
          postRequest('/reg',
          {
            username: _this.user.username,
            nickname: _this.user.nickname,
            password: _this.user.password
          },
          ).then(resp=> {
            _this.loading = false;
            if (resp.status == 200) {
              //成功
              var json = resp.data;
              if (json.status == 'success') {
                _this.$message({type:json.status,message:json.msg});
                _this.$router.replace({path: '/user'});
              } else if(json.status == 'error' ){
                _this.$alert(json.msg);
              }
            } else {
              //失败
              _this.$alert(json.msg);
            }
          }, resp=> {
            _this.loading = false;
            _this.$message({type:'error',message:'权限不足，请联系管理员！'});
          });
      },

      dakai:function(){
        dakai();
      },

      great: function () {
        document.getElementById('userface').onchange = function () {
            var prevDiv = document.getElementById('preview');
            if (this.files && this.files[0]) {
                var reader = new FileReader();
                reader.onload = function (evt) {
                    prevDiv.innerHTML = '<img width="100" height="100" src="' + evt.target.result + '" style="border-radius: 50%;" />';
                }
                reader.readAsDataURL(this.files[0]);
                var formData = new FormData();
                   formData.append('userface',this.files[0]);
                        uploadFileRequest("/uploaduserface", formData).then(resp=> {
                          var _this = this;
                          var json = resp.data;
                          if (json.status == 'success') {
                            // this.$refs.md.$imgUpdateByUrl(pos, json.msg)
                            _this.$message({type: json.status, message: json.msg});
                          } else {
                            _this.$message({type: json.status, message: json.msg});
                          }
                        });
            } else {
                prevDiv.innerHTML = '<div class="img" style="filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src=\'' + file.value + '\'"></div>';
            }
        }
      },
    },
    mounted: function(){
       this.great();
      }
  }

</script>
<style>
  .login-container {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }

  .login_title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
  }

  .login_remember {
    margin: 0px 0px 35px 0px;
    text-align: left;
  }

  #preview{
  	width: 100px;
  	height: 100px;
  	background: lightgray;
  	border-radius: 50%;
    margin-left: 120px;
  }
</style>
