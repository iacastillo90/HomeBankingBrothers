const app = Vue.createApp({
  data() {
    return {
      clienteName: '',
      accountsNumber1: '',
      vin001Balance: '',
      vin001CreationData: '',
      accountsNumber2: '',
      vin002Balance: '',
      vin002CreationData: ''
    };
  },
  created() {
    // Llama a la función loadData para obtener los datos del cliente y las cuentas
    this.loadData();
  },
  methods: {
loadData() {
  axios.get('/api/clients')
    .then(response => {
      const data = response.data; // Assuming the response has a structure like { clienteName, accounts }
      this.clienteName = data.clienteName;
      if (data.accounts && data.accounts.length >= 2) {
        this.accountsNumber1 = data.accounts[0].number;
        this.vin001Balance = data.accounts[0].balance;
        this.vin001CreationData = data.accounts[0].creationDate;
        this.accountsNumber2 = data.accounts[1].number;
        this.vin002Balance = data.accounts[1].balance;
        this.vin002CreationData = data.accounts[1].creationDate;
      }
    })
        .catch(error => {
          console.error('Error al obtener los datos del cliente y las cuentas:', error);
        });
    }
  }
});

app.mount('#app');
