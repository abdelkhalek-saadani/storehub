import { EXISTENT_USER } from '../../support/seed-data';

describe('Seed demo data', () => {
  const password = 'Passw0rd!';

  const storeNames = [
    'Urban Pantry',
    'Tunisian Store',
    'Fresh & Local Market',
    'Daily Essentials',
    'Green Basket',
    'Tunisian Delights',
    'The Healthy Corner',
    'Home & More',
    'Quick Grocery',
    'Neighborhood Market',
  ];

  const users = [
    {
      email: EXISTENT_USER.email,
      password,
      firstName: 'Abdelkhalek',
      lastName: 'Demo Owner',
      address: '123 Main St',
      phoneNumber: '+21600000000',
    },
    ...Array.from({ length: 9 }, (_, i) => ({
      email: `demo.owner${i + 2}@gmail.com`,
      password,
      firstName: 'Demo',
      lastName: `Store Owner ${i + 2}`,
      address: `${456 + i} Demo Street`,
      phoneNumber: `+2160000000${i + 1}`,
    })),
  ];

  // Meaningful, visually recognizable products for the GIF.
  const products = [
    ['Organic Bananas', 3.5, 'https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e', false],
    [
      'Fresh Strawberries',
      6.9,
      'https://images.unsplash.com/photo-1464965911861-746a04b4bca6',
      false,
    ],
    ['Avocado Hass', 4.8, 'https://images.unsplash.com/photo-1523049673857-eb18f1d7b578', false],
    ['Greek Yogurt', 5.2, 'https://images.unsplash.com/photo-1488477181946-6428a0291777', false],
    [
      'Almond Milk',
      7.5,
      'https://24hoursmarket.com/wp-content/uploads/2023/09/1693944798040.jpg',
      false,
    ],
    [
      'Whole Grain Bread',
      3.9,
      'https://images.unsplash.com/photo-1509440159596-0249088772ff',
      false,
    ],
    ['Free Range Eggs', 8.9, 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f', false],
    [
      'Extra Virgin Olive Oil',
      18.5,
      'https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5',
      false,
    ],
    [
      'Natural Orange Juice',
      6.5,
      'https://images.unsplash.com/photo-1600271886742-f049cd451bba',
      false,
    ],
    [
      'Arabica Coffee Beans',
      14.9,
      'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085',
      false,
    ],
    [
      'Dark Chocolate',
      4.5,
      'https://plus.unsplash.com/premium_photo-1670426501227-450cb0d92a16',
      false,
    ],
    ['Raw Honey', 12.9, 'https://images.unsplash.com/photo-1587049352846-4a222e784d38', false],
    [
      'Classic Margherita Pizza',
      12.9,
      'https://images.unsplash.com/photo-1574071318508-1cdbab80d002',
      true,
    ],
    [
      'Chicken Caesar Salad',
      14.5,
      'https://images.unsplash.com/photo-1546793665-c74683f339c1',
      true,
    ],
    ['Beef Burger', 16.9, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd', true],
    [
      'Grilled Chicken Wrap',
      11.9,
      'https://images.unsplash.com/photo-1565299507177-b0ac66763828',
      true,
    ],
    [
      'Creamy Mushroom Pasta',
      15.5,
      'https://images.unsplash.com/photo-1473093295043-cdd812d0e601',
      true,
    ],
    [
      'Chocolate Lava Cake',
      8.9,
      'https://images.unsplash.com/photo-1606313564200-e75d5e30476c',
      true,
    ],
    [
      'Fresh Berry Cheesecake',
      9.5,
      'https://images.unsplash.com/photo-1565958011703-44f9829ba187',
      true,
    ],
    [
      'Crispy Chicken Sandwich',
      13.9,
      'https://images.unsplash.com/photo-1550507992-eb63ffee0847',
      true,
    ],
    ['Mediterranean Bowl', 14.9, 'https://images.unsplash.com/photo-1547592180-85f173990554', true],
    [
      'Tuna Avocado Sandwich',
      12.5,
      'https://images.unsplash.com/photo-1528735602780-2552fd46c7af',
      true,
    ],
    ['Fresh Fruit Bowl', 9.9, 'https://images.unsplash.com/photo-1490474418585-ba9bad8fd0ea', true],
    [
      'Brownies',
      6.5,
      'https://carolinasbrownies.com/cdn/shop/files/box_6_brownies_yellow_background_1200x.png',
      true,
    ],
  ];

  let predictedStoreToken: string;

  it('creates demo users and stores', () => {
    users.forEach((user, index) => {
      cy.signup(user).then(() => {
        cy.login(user.email, user.password).then((token) => {
          cy.createStore(token, {
            name: storeNames[index],
            description:
              index === 0
                ? 'Your personalized shopping destination'
                : `${storeNames[index]} demo store`,
            address: user.address,
          }).then((res) => {
            expect(res.status).to.eq(201);
          });
        });
      });
    });
  });

  it('creates regular products in Predicted Store', () => {
    cy.login(EXISTENT_USER.email, password).then((token) => {
      predictedStoreToken = token;
    });
    products.forEach(([name, unitPrice, imageUrl, isBestSaller], index) => {
      cy.then(() => predictedStoreToken).then((token) => {
        cy.createProduct(token, {
          name: name as string,
          unitPrice: unitPrice as number,
          initialQty: 9999,
          imageUrl: imageUrl as string,
          isBestSeller: isBestSaller as boolean,
        }).then((res) => {
          expect(res.status).to.eq(201);
        });
      });
    });
  });

  it('creates weekday slot configs for Predicted Store', () => {
    const weekdays = [
      { day: 1, name: 'Monday' },
      { day: 2, name: 'Tuesday' },
      { day: 3, name: 'Wednesday' },
      { day: 4, name: 'Thursday' },
      { day: 5, name: 'Friday' },
    ];

    weekdays.forEach(({ day }) => {
      cy.then(() => predictedStoreToken).then((token) => {
        cy.createSlotConfig(token, {
          dayOfWeek: day,
          startTime: '09:00',
          endTime: '18:00',
          slotDurationMin: 30,
          maxCapacity: 10,
          cutoffMinutes: 60,
          active: true,
        }).then((res) => {
          expect(res.status).to.eq(200);
        });
      });
    });
  });

  const saleEvents = [
    ['Mother’s Day', 'https://images.unsplash.com/photo-1619903180479-6602d32de99c'],
    ['Summer Fresh Deals', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e'],
    ['Back to School Savings', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b'],
    ['Customer Favorites Sale', 'https://images.unsplash.com/photo-1607083206968-13611e3d76db'],
    ['Weekend Family Deals', 'https://images.unsplash.com/photo-1586426006315-b11fa075a76a'],
    ['Healthy Living Sale', 'https://images.unsplash.com/photo-1498837167922-ddd27525d352'],
  ];

  it('creates sale events', () => {
    cy.login(EXISTENT_USER.email, password).then((token) => {
      predictedStoreToken = token;
    });
    saleEvents.forEach(([name, imageUrl]) => {
      cy.then(() => predictedStoreToken).then((token) => {
        cy.request({
          method: 'POST',
          url: `${Cypress.env('catalogServiceUrl')}/api/sale-events`,
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: {
            name,
            imageUrl,
          },
        }).then((res) => {
          expect(res.status).to.eq(201);
        });
      });
    });
  });

  const subcategories = [
    ['Fresh Produce', 'https://images.unsplash.com/photo-1610832958506-aa56368176cf'],
    ['Bakery ', 'https://images.unsplash.com/photo-1509440159596-0249088772ff'],
    ['Dairy ', 'https://images.unsplash.com/photo-1628088062854-d1870b4553da'],
    ['Meat ', 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f'],
    ['Pantry ', 'https://images.unsplash.com/photo-1606787366850-de6330128bfc'],
    ['Snacks ', 'https://images.unsplash.com/photo-1621939514649-280e2ee25f60'],
    ['Beverages', 'https://images.unsplash.com/photo-1544145945-f90425340c7e'],
    ['Ready Meals', 'https://images.unsplash.com/photo-1547592180-85f173990554'],
  ];

  it('creates 8 subcategories', () => {
    cy.login(EXISTENT_USER.email, password).then((token) => {
      predictedStoreToken = token;
    });
    subcategories.forEach(([name, imageUrl]) => {
      cy.then(() => predictedStoreToken).then((token) => {
        cy.request({
          method: 'POST',
          url: `${Cypress.env('catalogServiceUrl')}/api/categories/subcategories`,
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: {
            name,
            imageUrl,
          },
        }).then((res) => {
          expect(res.status).to.eq(201);
        });
      });
    });
  });
});
