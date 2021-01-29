<template>
  <el-container>
    <el-header class="cate_mana_header">
      <el-input
        placeholder="通过所属文章搜索评论..."
        v-model.trim="keywords1" style="width: 200px;">
      </el-input>
      <el-input
        placeholder="通过评论人搜索评论..."
        v-model.trim="keywords" style="width: 200px;">
      </el-input>
      <el-button type="primary" size="medium" icon="el-icon-search" style="margin-left: 10px" @click="searchClick">搜索</el-button>
    </el-header>
    <el-main class="cate_mana_main">
      <el-table
        ref="multipleTable"
        :data="comments"
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
          label="所属文章"
          width="120" align="left">
          <template slot-scope="scope"><span style="color: #409eff;cursor: pointer" @click="itemClick(scope.row)">{{ scope.row.title}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="评论人"
          prop="nickname"
          width="120" align="left">
        </el-table-column>
        <el-table-column
          label="评论内容"
          width="120" align="left">
          <template slot-scope="scope">
            <el-popover
              placement="right"
              width="400"
              trigger="click">
                <span>{{scope.row.content}}</span>
                <span v-show="scope.row.content.length > 10" style="color: #909399;cursor: pointer" slot="reference">
                  {{ scope.row.content.substring(0,10)}}...
                </span>
                <span v-show="scope.row.content.length < 10" style="color: #909399;cursor: pointer" slot="reference">
                  {{ scope.row.content.substring(0,10)}}
                </span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column
          prop="publishDate"
          label="评论时间" align="left">
          <template slot-scope="scope">{{ scope.row.publishDate | formatDateTime}}</template>
        </el-table-column>
        <el-table-column label="操作" align="left">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button type="danger" :disabled="this.selItems.length==0" style="margin-top: 10px;width: 100px;"
                 @click="deleteAll" v-if="this.comments.length>0">批量删除
      </el-button>
      <span></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount" @current-change="currentChange" v-show="this.comments.length>0">
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
      searchClick(){
        this.refresh(1, this.pageSize);
      },
      itemClick(row){
        this.$router.push({path: '/blogDetail', query: {aid: row.aid}})
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
          _this.deleteComment(ids.substring(0, ids.length - 1));
        }).catch(() => {
          //取消
          _this.loading = false;
          _this.$message({type:'info',message:'已取消删除'});
        });
      },
      handleSelectionChange(val) {
        this.selItems = val;
      },
      handleDelete(index, row){
        let _this = this;
        this.$confirm('确认删除该评论？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          _this.deleteComment(row.id);
        }).catch(() => {
          //取消
          _this.loading = false;
          _this.$message({type:'info',message:'已取消删除'});
        });
      },
      deleteComment(ids){
        var _this = this;
        this.loading = true;
        //删除
        deleteRequest("/admin/comment/deleteComments/" + ids).then(resp=> {
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
        getRequest("/admin/comment/getAllComments"+"?page="+page+"&count="+count+"&keywords="+this.keywords+"&keywords1="+this.keywords1).then(resp=> {
          _this.loading = false;
          _this.comments = resp.data.comments;
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
        keywords: '',
        keywords1: '',
        comments: [],
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
