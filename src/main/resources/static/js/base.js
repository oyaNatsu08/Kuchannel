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
    created: function () {
    }
}).mount('#app')