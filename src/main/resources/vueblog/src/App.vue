<template>
  <div id="app" style="background-color: rgba(235, 235, 235, 0.08)">
    <router-view v-if="isRouteAlive"/>
  </div>
</template>

<script>
export default {
  name: 'app',
  data(){
    return {
      isRouteAlive:true
    }
  },
  provide(){
    return {
      reload: this.reload,
    };
  },
  methods: {
    reload(){
      this.isRouteAlive = false;
      this.$nextTick(function(){
        this.isRouteAlive = true;
      })
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
