<template>
  <el-dropdown trigger="click" @command="handleSetLang">
    <div class="lang-select">
      <svg-icon class-name="lang-icon" icon-class="language" />
      <span class="lang-label">{{ currentLabel }}</span>
      <i class="el-icon-caret-bottom lang-caret" />
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item
        v-for="item in langOptions"
        :key="item.value"
        :disabled="item.value === currentLang"
        :command="item.value"
      >
        {{ item.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
import { langOptions, getLang, setLang } from '@/i18n'

export default {
  name: 'LangSelect',
  data() {
    return {
      langOptions
    }
  },
  computed: {
    currentLang() {
      return this.$i18n.locale || getLang()
    },
    currentLabel() {
      const found = this.langOptions.find(item => item.value === this.currentLang)
      return found ? found.label : this.currentLang
    }
  },
  methods: {
    handleSetLang(lang) {
      setLang(lang)
      this.$message({
        message: this.$t('common.success'),
        type: 'success'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.lang-select {
  display: inline-flex;
  align-items: center;
  height: 100%;
  cursor: pointer;
  color: #5a5e66;

  .lang-icon {
    font-size: 16px;
  }

  .lang-label {
    display: inline-block;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 13px;
    margin: 0 4px;
  }

  .lang-caret {
    font-size: 12px;
  }
}
</style>
