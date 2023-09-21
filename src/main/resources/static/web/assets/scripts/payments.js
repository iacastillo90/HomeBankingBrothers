// app.js
new Vue({
    el: '#app',
    data: {
        cardNumber: '', // Nombre del campo actualizado a cardNumber
        cvv: '',        // Nombre del campo actualizado a cvv
        amount: '',
        description: '', // Nombre del campo actualizado a description
        name: '',        // Nombre del campo actualizado a name
        thruDate: '',    // Nombre del campo actualizado a thruDate
        message: '',
        error: ''
    },
    methods: {
        makePayment() {
            // Convierte la fecha del formato DD-MM-YYYY a YYYY-MM-DD
            const formattedThruDate = this.thruDate.split('-').reverse().join('-');
    
            // Construye el objeto de datos para enviar al servidor
            const paymentData = {
                number: this.cardNumber,
                cvv: this.cvv,
                amount: this.amount,
                description: this.description,
                name: this.name,
                thruDate: formattedThruDate
            };
    
            // Envía la solicitud al servidor utilizando Axios
            axios.post('/api/payments', paymentData)
                .then(response => {
                    // Maneja la respuesta exitosa
                    this.message = response.data;
                    this.error = '';
                })
                .catch(error => {
                    // Maneja el error de la solicitud
                    this.error = error.response.data;
                    this.message = '';
                });
        }
    }
});
