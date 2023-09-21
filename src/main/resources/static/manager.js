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
      axios
        .post('/api/admin/loan', this.newLoanType)
        .then(response => {
          // Handle success, e.g., show a success message to the user
          console.log('Loan type created successfully:', response.data);
          this.successMessage = 'Loan type created successfully'; // Asigna el mensaje de éxito
          this.errorMessage = ''; // Limpia el mensaje de error

          // Clear the form fields
          this.newLoanType = {
            name: '',
            maxAccount: 0,
            payments: [],
            interest: 0.0,
          };
        })
        .catch(error => {
          // Handle error, e.g., show an error message to the user
          console.error('Error creating loan type:', error);
          this.errorMessage = 'Error creating loan type'; // Asigna el mensaje de error
          this.successMessage = ''; // Limpia el mensaje de éxito
        });
    },
  },
});

app.mount('#app');
