<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'weimes-git',
  data() {
    return {
      url: 'https://123app.tech'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>