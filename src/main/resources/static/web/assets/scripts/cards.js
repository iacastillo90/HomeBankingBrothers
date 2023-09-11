const app = Vue.createApp({
    data() {
        return {
            cards: [],
            checkType: null,
            checkColor: null,
            fromDate: "",
            thruDate: "",
              condition: true,
        };
    },
    created() {
        this.loadData();
        const urlParams = new URLSearchParams(window.location.search);
        var myParam = urlParams.get("id");
    },
    methods: {
        loadData() {
            axios.get('http://localhost:8080/api/clients/current')
                .then(response => {
                    const client = response.data;
                    if (client && client.cards) {
                        this.cards = client.cards;
                        this.fromDate = response.data.cards.map(card => card.fromDate.slice(2, 7).replace(/-/g, '/'))
                        this.thruDate = response.data.cards.map(card => card.thruDate.slice(2, 7).replace(/-/g, '/'))
                        console.log(this.cards);
                    } else {
                        console.error('La respuesta de la solicitud está vacía o no tiene la propiedad "cards".');
                    }
                })
                .catch(error => {
                    console.error('Error al obtener los datos de las tarjetas:', error);
                });
        },
        newCard() {
            // Registra los valores antes de enviar la solicitud
            console.log('checkType:', this.checkType);
            console.log('checkColor:', this.checkColor);

            axios.post("/api/clients/current/cards", null, {
                params: {
                    cardType: this.checkType,
                    cardColor: this.checkColor
                }
            })
            .then(response => {
                window.alert("La tarjeta se ha creado con éxito");
                console.log("CREADO");
                return window.location.reload();
            })
            .catch(error => {
                console.error('Error al crear la tarjeta:', error);
            });
        },
        deleteCards(id) {
            axios.patch(`/api/clients/current/cards/delete/` + id)
                .then(response => {
                    return window.location.reload()
                })
        },
        logout() {
            axios.post(`http://localhost:8080/api/logout`)
              .then(response => {
                // Redirige al usuario a la página de inicio de sesión
                window.location.href = '/web/Index.html';
              })
              .catch(error => {
                console.error('Error al cerrar sesión', error);
              });
          },
        getCardClass(cardType) {
            // Define your logic to determine the card class based on cardType
            // For example:
            if (cardType === 'DEBITO') {
                return 'debit-card';
            } else if (cardType === 'CREDITO') {
                return 'credit-card';
            } else {
                return 'default-card';
            }
        },
    },
});

app.mount('#app');
