<template>
  <el-container>
    <el-header class="cate_mana_header">
      <el-input
        placeholder="请输入栏目名称"
        v-model.trim="cateName" style="width: 200px;">
      </el-input>
      <el-button type="primary" size="medium" style="margin-left: 10px" @click="addNewCate">新增栏目</el-button>
    </el-header>
    <el-main class="cate_mana_main">
      <el-table
        ref="multipleTable"
        :data="categories"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange" v-loading="loading">
        <el-table-column
          type="selection"
          width="55" align="left">
        </el-table-column>
        <el-table-column
          label="编号"
          prop="id"
          width="120" align="left">
        </el-table-column>
        <el-table-column
          label="栏目名称"
          prop="cateName"
          width="120" align="left">
        </el-table-column>
        <el-table-column
          prop="date"
          label="启用时间" align="left">
          <template slot-scope="scope">{{ scope.row.date | formatDate}}</template>
        </el-table-column>
        <el-table-column label="操作" align="left">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="danger" :disabled="this.selItems.length==0" style="margin-top: 10px;width: 100px;"
                 @click="deleteAll" v-if="this.categories.length>0">批量删除
      </el-button>
      <span></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount" @current-change="currentChange" v-show="this.categories.length>0">
      </el-pagination>
    </el-main>
  </el-container>
</template>
<script>
  import {postRequest} from '../utils/api'
  import {putRequest} from '../utils/api'
  import {deleteRequest} from '../utils/api'
  import {getRequest} from '../utils/api'
  export default{
    methods: {
      addNewCate(){
        this.loading = true;
        var _this = this;
        postRequest('/admin/category/', {cateName: this.cateName}).then(resp=> {
          if (resp.status == 200) {
            var json = resp.data;
            _this.$message({type: json.status, message: json.msg});
            _this.cateName = '';
            _this.refresh(1,this.pageSize);
          }
          _this.loading = false;
        }, resp=> {
          if (resp.response.status == 403) {
            _this.$message({
              type: 'error',
              message: resp.response.data
            });
          }
          _this.loading = false;
        });
      },
      deleteAll(){
        var _this = this;
        this.$confirm('确认删除这 ' + this.selItems.length + ' 条数据?', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(()=> {
          var selItems = _this.selItems;
          var ids = '';
          for (var i = 0; i < selItems.length; i++) {
            ids += selItems[i].id + ",";
          }
          _this.deleteCate(ids.substring(0, ids.length - 1));
        }).catch(() => {
          //取消
          _this.loading = false;
          _this.$message({type:'info',message:'已取消删除'});
        });
      },
      handleSelectionChange(val) {
        this.selItems = val;
      },
      handleEdit(index, row){
        var _this = this;
        this.$prompt('请输入新名称', '编辑', {
          confirmButtonText: '更新',
          inputValue: row.cateName,
          cancelButtonText: '取消'
        }).then(({value}) => {
          //value就是输入值
          if (value == null || value.trim().length == 0) {
            _this.$message({
              type: 'info',
              message: '数据不能为空!'
            });
          } else {
            _this.loading = true;
            putRequest("/admin/category/", {id: row.id, cateName: value}).then(resp=> {
              var json = resp.data;
              _this.$message({
                type: json.status,
                message: json.msg
              });
              _this.refresh(1,this.pageSize);
            }, resp=> {
              if (resp.response.status == 403) {
                _this.$message({
                  type: 'error',
                  message: resp.response.data
                });
              }
              _this.loading = false;
            });
          }
        }).catch(() => {
            _this.loading = false;
            _this.$message({type:'info',message:'已取消编辑'});
        });
      },
      handleDelete(index, row){
        let _this = this;
        this.$confirm('确认删除 ' + row.cateName + ' ?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.deleteCate(row.id);
        }).catch(() => {
          //取消
          _this.loading = false;
          _this.$message({type:'info',message:'已取消删除'});
        });
      },
      deleteCate(ids){
        var _this = this;
        this.loading = true;
        //删除
        deleteRequest("/admin/category/" + ids).then(resp=> {
          var json = resp.data;
          _this.$message({
            type: json.status,
            message: json.msg
          });
          _this.refresh(this.currentPage,this.pageSize);
        }, resp=> {
          _this.loading = false;
          if (resp.response.status == 403) {
            _this.$message({
              type: 'error',
              message: resp.response.data
            });
          // } else if (resp.response.status == 500) {
          //   _this.$message({
          //     type: 'error',
          //     message: '该栏目下尚有文章，删除失败!'
          //   });
          }
        })
      },
      //翻页
      currentChange(currentPage){
        this.currentPage = currentPage;
        this.loading = true;
        this.refresh(currentPage, this.pageSize);
      },
      refresh(page,count){
        let _this = this;
        getRequest("/admin/category/all"+"?page="+page+"&count="+count).then(resp=> {
          _this.loading = false;
          _this.categories = resp.data.categories;
          _this.totalCount = resp.data.totalCount;

        }, resp=> {
          if (resp.response.status == 403) {
            _this.$message({
              type: 'error',
              message: resp.response.data
            });
          }
          _this.loading = false;
        });
      }
    },
    mounted: function () {
      var _this = this;
      _this.loading = true;
      _this.refresh(1, this.pageSize);
      window.bus.$on('blogTableReload', function () {
        _this.loading = true;
        _this.refresh(_this.currentPage, _this.pageSize);
      })
      // this.loading = true;
      // this.refresh(this.currentPage, this.pageSize);
    },
    data(){
      return {
        cateName: '',
        categories: [],
        selItems: [],
        currentPage: 1,
        totalCount: -1,
        pageSize: 6,
        loading: false
      }
    }
  }
</script>
<style>
  .cate_mana_header {
    background-color: #ececec;
    margin-top: 20px;
    padding-left: 5px;
    display: flex;
    justify-content: flex-start;
  }

  .cate_mana_main {
    /*justify-content: flex-start;*/
    display: flex;
    flex-direction: column;
    padding-left: 5px;
    background-color: #ececec;
    margin-top: 20px;
    padding-top: 10px;
  }
</style>
