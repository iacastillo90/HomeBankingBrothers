const app = new Vue({
    el: '#app',
    data: {
        paymentData: {
            number: '',
            amount: 0,         // Asegúrate de que el tipo sea un número
            cvv: 0,            // Asegúrate de que el tipo sea un número entero
            description: '',
            thruDate: ''
        },
        paymentResult: ''
    },
    methods: {
        submitPayment() {
            // Construye la URL completa del servidor donde se encuentra tu servlet
            const serverURL = 'http://localhost:8080'; // Cambia la dirección según tu configuración

            // Realizar una solicitud HTTP POST para enviar los datos del pago al servlet
            axios.post(serverURL + '/api/payments', this.paymentData)
                .then(response => {
                    console.log(this.paymentData);
                    // Manejar la respuesta del servidor
                    if (response.status === 200) {
                        this.paymentResult = 'Transacción completada con éxito';
                    } else {
                        this.paymentResult = 'Error: ' + response.data;
                    }
                })
                .catch(error => {
                    this.paymentResult = 'Error al procesar la solicitud';
                    console.error(error);
                });
        }
    }
});

