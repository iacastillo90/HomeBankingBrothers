const { createApp } = Vue;

const options = {
  data() {
    return {
      color: "",
      type: "",
    };
  },
  created() {},
  methods: {
    alert() {
      let mensaje;
      let opcion = confirm("Do you want to create a new card?");
      if (opcion == true) {
        axios
          .post(
            'http://localhost:8080/api/clients/current/cards',`color=${this.color}&type=${this.type}`,{headers:{'content-type': 'application/x-www-form-urlencoded'}}
          )
          .then((response) => {
            location.href = "/web/assets/pages/cards.html";
          })
          .catch((error) => {
            window.alert("You have reached the card limit");
          });
      }
    },
  },
};

const app = createApp(options);
app.mount("#app");
