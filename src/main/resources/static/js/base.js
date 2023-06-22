const app = Vue.createApp({
  data() {
    return {
      showMenu: false,
    }
  },
  methods: {
    toggleMenu() {
      this.showMenu = !this.showMenu;
    }
  },
});

app.mount('#app0');