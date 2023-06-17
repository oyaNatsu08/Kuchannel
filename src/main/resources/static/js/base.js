const { createApp } = Vue

createApp({
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
}).mount('#app0')