<template>
  <div class="modal" v-if="show">
    <div class="wrap" @click="close">
      <div class="box">
        <div class="modal-box__header">{{ this.$t('common.preview') }}</div>
        <div class="preview-body" style="max-height: 50vh; overflow: auto">
          <div class="container"></div>
        </div>
        <div class="modal-box__footer">
          <button class="primary" @click="close">{{ this.$t('common.confirm') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "start-preview",
  data() {
    return {
      show: false,
    };
  },
  methods: {
    close() {
      this.show = false;
    },
    showModal(...html) {
      this.show = true;
      setTimeout(() => {
        $('.container').empty().html(html);
      }, 200);
    }
  }
};
</script>

<style>
/* 不同模板 间隙 */
.container .hiprint-printTemplate {
  background: #fff;
  border-bottom: 10px solid #ccc;
}
/* 批量打印 间隙 */
.container .hiprint-printTemplate .hiprint-printPanel:not(:last-of-type) {
  border-bottom: 5px solid #ccc;
}
</style>

<style scoped>
.preview-body {
  background: #ccc;
  padding: 14px 0;
  display: flex;
  justify-content: center;
}
</style>
