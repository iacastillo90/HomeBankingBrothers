const { createApp } = Vue;

createApp({
    data() {
        return {
            cardNumber: '',
            cvv: '',
            amount: '',
            description: '',
            name: '',
            thruDate: '',
            message: '',
            error: ''
        };
    },
    methods: {
        makePayment() {
            const formattedThruDate = this.thruDate.split('-').reverse().join('-');
            const paymentData = {
                number: this.cardNumber,
                cvv: this.cvv,
                amount: this.amount,
                description: this.description,
                name: this.name,
                thruDate: formattedThruDate
            };

            console.log('Payment Data:', paymentData);

            axios.post('/api/payments', paymentData, {
                headers: {
                    'Content-Type': 'application/json', // Asegura que los datos se envíen como JSON
                   
                },
            })
            .then(response => {
                Swal.fire({
                    icon: 'success',
                    title: 'Payment Successful',
                    text: response.data,
                });
                this.error = '';
            })
            .catch(error => {
                console.error('Error:', error);

                Swal.fire({
                    icon: 'error',
                    title: 'Payment Error',
                    text: error.response.data,
                });
                this.message = '';
            });
        }
    }    
}).mount("#app");
