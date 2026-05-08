import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/usr': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/goods': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/cart': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/order': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/wx': {
        target: 'http://localhost:10010',
        changeOrigin: true
      },
      '/seckill': {
        target: 'http://localhost:10010',
        changeOrigin: true
      }
    }
  }
})
