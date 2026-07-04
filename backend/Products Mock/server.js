import express from 'express'

const app = express()
app.use(express.json())

app.post('/prices', (req, res) => {
console.log('Received request body:', JSON.stringify(req.body, null, 2));
  res.json({
    items: [
      {
        productId: "11111111-1111-1111-1111-111111111111",
        quantity: 3,
        unitPrice: 2.50,
        originalLineTotal: 7.50,
        discountAmount: 2.50,
        finalLineTotal: 5.00,
        appliedOffer: {
          offerId: "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
          label: "Buy 2 get 1 free",
          type: "BUY_X_GET_Y_FREE"
        }
      },
      {
        productId: "22222222-2222-2222-2222-222222222222",
        quantity: 1,
        unitPrice: 1.20,
        originalLineTotal: 1.20,
        discountAmount: 0.00,
        finalLineTotal: 1.20,
        appliedOffer: null
      }
    ],
    originalTotal: 20,
    finalTotal: 15,
    totalDiscount: 5,
    
  })
})

app.listen(8082, () => console.log('Catalog mock running on http://localhost:8082'))
