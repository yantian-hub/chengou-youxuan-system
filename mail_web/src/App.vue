<template>
  <div id="app-root">
    <NavHeader v-if="route.meta.showNav !== false" />
    <router-view />
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavHeader from './components/NavHeader.vue'

const route = useRoute()
const router = useRouter()

watch(() => route.path, () => {
  document.documentElement.scrollTop = 0
})
</script>

<style>
body {
  margin: 0;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
}
#app-root {
  min-height: 100vh;
}
</style>
