import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// import App from './App.vue'
import console from './views/console'
import JsonViewer from 'vue-json-viewer'
import router from './router'

Vue.use(ElementUI)

Vue.use(JsonViewer)

new Vue({
  el: '#app',
  router,
  render: h => h(console)
})
