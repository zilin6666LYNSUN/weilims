<template>
  <div class="app-container">
    <!-- <div v-html="jmReport"></div> -->
    <iframe v-if="pageUrl" :src="pageUrl" style="width: 100%; height: 500px; border: none;"></iframe>
  </div>
</template>

<script>
import { list } from "@/api/report/jmreport";


export default {
  name: "JMReport",
  data() {
    return {
      jmReport: '',
      pageUrl: '',
    }
  },
  created() {
    this.loadPage();
  },
  methods: {
    loadPage() {
      this.loading = true;
      list().then(response => {
          this.pageUrl = response.msg;
      });
    }
  }
};
</script>
