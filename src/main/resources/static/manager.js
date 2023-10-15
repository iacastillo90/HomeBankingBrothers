const app = Vue.createApp({
  data() {
    return {
      newLoanType: {
        name: '',
        maxAccount: 0,
        payments: [],
        interest: 0.0,
      },
      successMessage: '', // Agrega una propiedad para almacenar el mensaje de éxito
      errorMessage: '', // Agrega una propiedad para almacenar el mensaje de error
    };
  },

  methods: {
    createLoanType() {
      // Convierte el campo "payments" a un array
      this.newLoanType.payments = this.newLoanType.payments.split(',').map(payment => parseInt(payment.trim()));
  
      axios
        .post('/api/admin/loan', this.newLoanType, {
          headers: {
            'Content-Type': 'application/json',
          },
        })
        .then(response => {
          console.log('Loan type created successfully:', response.data);
          this.successMessage = 'Loan type created successfully';
          this.errorMessage = '';
          this.newLoanType = {
            name: '',
            maxAccount: 0,
            payments: [],
            interest: 0.0,
          };
        })
        .catch(error => {
          console.error('Error creating loan type:', error);
          this.errorMessage = 'Error creating loan type';
          this.successMessage = ''; 
        });
    },
  },  
});

app.mount('#app');
