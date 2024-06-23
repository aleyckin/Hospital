<template>
    <div>
      <!-- Условный рендеринг Header -->
      <Header v-if="showHeader"></Header>
      <router-view></router-view>
    </div>
  </template>
  
  <script>
  import Header from "./components/Header.vue";
  
  export default {
    components: {
      Header
    },
    computed: {
      showHeader() {
        // Список маршрутов, где не нужно отображать Header
        const noHeaderRoutes = ['/reset-password'];
  
        return !noHeaderRoutes.includes(this.$route.path);
      }
    }
  }
  </script>
  