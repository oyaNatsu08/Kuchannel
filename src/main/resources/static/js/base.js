const { createApp } = Vue

createApp({
   data() {
       return {
           showMenu: false,
           userImage: '',
       }
   },
   methods: {
       toggleMenu() {
           this.showMenu = !this.showMenu;
       }
   },
   created: function() {
      // this.userImage = `[[ ${image} ]]`;
   }
}).mount('#app0')