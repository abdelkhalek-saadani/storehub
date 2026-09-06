const ORDER_PORT = process.env.ORDER_PORT || '8090';
const orderUrl = `http://localhost:${ORDER_PORT}`;
const KC_PORT = process.env.KC_PORT || '8088';
const KC_HOSTNAME = process.env.KC_HOSTNAME || 'auth-server';
const keycloakUrl = `http://${KC_HOSTNAME}:${KC_PORT}`;
const CATALOG_PORT = process.env.CATALOG_PORT || '8100';
const catalogUrl = `http://localhost:${CATALOG_PORT}`;

console.log('Seed demo data');

const ownerEmail = 'abdelkhalek@gmail.com';
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
        email: ownerEmail,
        password,
        firstName: 'Abdelkhalek',
        lastName: 'Demo Owner',
        address: '123 Main St',
        phoneNumber: '+21600000000',
    },
    ...Array.from({length: 9}, (_, i) => ({
        email: `demo.owner${i + 2}@gmail.com`,
        password,
        firstName: 'Demo',
        lastName: `Store Owner ${i + 2}`,
        address: `${456 + i} Demo Street`,
        phoneNumber: `+2160000000${i + 1}`,
    })),
];

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

const weekdays = [
    {day: 1, name: 'Monday'},
    {day: 2, name: 'Tuesday'},
    {day: 3, name: 'Wednesday'},
    {day: 4, name: 'Thursday'},
    {day: 5, name: 'Friday'},
];


const saleEvents = [
    ['Mother’s Day', 'https://images.unsplash.com/photo-1619903180479-6602d32de99c'],
    ['Summer Fresh Deals', 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e'],
    ['Back to School Savings', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b'],
    ['Customer Favorites Sale', 'https://images.unsplash.com/photo-1607083206968-13611e3d76db'],
    ['Weekend Family Deals', 'https://images.unsplash.com/photo-1586426006315-b11fa075a76a'],
    ['Healthy Living Sale', 'https://images.unsplash.com/photo-1498837167922-ddd27525d352'],
];


const subcategories = [
    ['Fresh', 'https://images.unsplash.com/photo-1610832958506-aa56368176cf'],
    ['Bakery ', 'https://images.unsplash.com/photo-1509440159596-0249088772ff'],
    ['Dairy ', 'https://images.unsplash.com/photo-1628088062854-d1870b4553da'],
    ['Meat ', 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f'],
    ['Pantry ', 'https://images.unsplash.com/photo-1606787366850-de6330128bfc'],
    ['Snacks ', 'https://images.unsplash.com/photo-1621939514649-280e2ee25f60'],
    ['Beverages', 'https://images.unsplash.com/photo-1544145945-f90425340c7e'],
    ['Ready', 'https://images.unsplash.com/photo-1547592180-85f173990554'],
];

async function seed() {
    console.log('creates demo users and stores');
    for (const [index, user] of users.entries()) {
        await signup(user);
        const token = await login(user.email, user.password);
        await createStore(token, {
            name: storeNames[index],
            description:
                index === 0
                    ? 'Your personalized shopping destination'
                    : `${storeNames[index]} demo store`,
            address: user.address,
        });
        console.log(`Seeded store for ${user.email}`);
    }

    console.log('Creates regular products in Predicted Store');
    const token = await login(ownerEmail, password);
    for (const [name, unitPrice, imageUrl, isBestSeller] of products) {
        await createProduct(token, {
            name: name,
            unitPrice: unitPrice,
            initialQty: 9999,
            imageUrl: imageUrl,
            isBestSeller: isBestSeller,
        })
    }
    console.log(`Seeded ${products.length} products`);

    console.log('creates weekday slot configs for Predicted Store');
    for (const {day} of weekdays) {
        await createSlotConfig(token, {
            dayOfWeek: day,
            startTime: '09:00',
            endTime: '18:00',
            slotDurationMin: 30,
            maxCapacity: 10,
            cutoffMinutes: 60,
            active: true,
        });
    }
    console.log(`Seeded ${weekdays.length} slot configs`);

    console.log('creates sale events');
    for (const [name, imageUrl] of saleEvents) {
        await createSaleEvent(token, {name, imageUrl});
    }
    console.log(`Seeded ${saleEvents.length} sale events`);

    console.log('creates 8 subcategories');
    for (const [name, imageUrl] of subcategories) {
        await createSubCategory(token, {name, imageUrl});
    }
    console.log(`Seeded ${saleEvents.length} subcategories`);


}


seed()
    .then(() => console.log('Seed complete.'))
    .catch((err) => {
        console.error('Seed failed:', err);
        process.exit(1);
    });

async function signup(user) {
    const res = await fetch(`${orderUrl}/api/auth/signup`, {
        method: 'POST',
        body: JSON.stringify(user),
        headers: {'Content-Type': 'application/json'},
    })

    if (res.status !== 201) {
        if (res.status === 409) {
            console.log('Conflict, user maybe already exists from previous script run');
        } else {
            throw new Error(`User creation failed for ${user.email}: ${res.status}`);
        }
    }
    return res;
}

async function login(email, password) {
    const res = await fetch(`${keycloakUrl}/realms/storehub/protocol/openid-connect/token`,
        {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: new URLSearchParams({
                grant_type: 'password',
                client_id: 'e2e-test-client',
                username: email,
                password: password
            })
        });
    if (!res.ok) throw new Error(`Login failed for ${email}: ${res.status}`);
    const data = await res.json();
    return data.access_token;

}

async function createStore(token, store) {
    const res = await fetch(`${orderUrl}/api/stores`,
        {
            method: 'POST',
            headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
            body: JSON.stringify(store),
        })
    if (res.status !== 201) {
        throw new Error(`Store creation failed for ${store.name}: ${res.status}`);
    }
    return res;
}

async function createProduct(token, product) {
    const res = await fetch(`${catalogUrl}/api/products`,
        {
            method: 'POST',
            headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
            body: JSON.stringify(product),
        })

    if (res.status !== 201) {
        throw new Error(`Product creation failed for ${product.name}: ${res.status}`);
    }
    return res;
}

async function createSlotConfig(token, config) {
    const res = await fetch(`${catalogUrl}/api/admin/slot-configs`,
        {
            method: 'POST',
            headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
            body: JSON.stringify(config),
        });
    if (res.status !== 200) {
        throw new Error(`Slot Config creation failed for ${config.dayOfWeek}: ${res.status}`);
    }
    return res;
}

async function createSaleEvent(token, saleEvent) {
    const res = await fetch(`${catalogUrl}/api/sale-events`, {
        method: 'POST',
        headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
        body: JSON.stringify(saleEvent)
    });
    if (res.status !== 201) {
        throw new Error(`Sale Event creation failed for ${saleEvent.name}`);
    }
    return res;
}

async function createSubCategory(token, subCategory) {
    const res = await fetch(`${catalogUrl}/api/categories/subcategories`,
        {
            method: 'POST',
            headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
            body: JSON.stringify(subCategory),
        });
    if (res.status !== 201) {
        throw new Error(`Subcategory creation failed for ${subCategory.name}`);
    }
    return res;
}