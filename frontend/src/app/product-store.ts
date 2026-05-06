import {patchState, signalMethod, signalStore, withComputed, withMethods, withState} from '@ngrx/signals';
import {Product} from './models/Product';
import {computed, inject, PLATFORM_ID} from '@angular/core';
import {produce} from 'immer';
import {Toaster} from './services/Toaster';
import {CartItem} from './models/CartItem';
import {withStorageSync} from '@angular-architects/ngrx-toolkit';
import {SignInParams, User} from './models/User';
import {Order} from './models/Order';
import {AddReviewParam} from './models/Review';
import {isPlatformBrowser} from '@angular/common';
import {SeoManager} from './services/seo-manager';

export type ProductState = {
  products: Product[];
  category: string;
  wishlist: Product[],
  cartItems: CartItem[],
  user: User | null,
  isLoading: boolean,
  order?: Order
  /**
   * @deprecated No longer used. Kept for reference
   * Controls the visibility of the main layout sidenav.
   * Bound to Angular Material `<mat-sidenav [opened]>`.
   */
  isSideNavOpen: boolean
};

const initialState: ProductState = {
  products: [
    {
      id: "p1",
      name: "Wireless Noise-Canceling Headphones",
      description: "High-quality over-ear headphones with active noise cancelation and 30-hour battery life.",
      price: 199.99,
      imageUrl: "https://images.unsplash.com/photo-1612478120679-5b7412e15f84",
      rating: 4.7,
      reviewCount: 842,
      inStock: true,
      category: "electronics",
      reviews: [
        {
          name: "Alex Brown",
          avatar: "https://randomuser.me/api/portraits/men/11.jpg",
          rating: 5,
          date: "2024-01-18",
          title: "Amazing sound",
          comment: "Noise canceling works perfectly, especially for travel."
        },
        {
          name: "Sarah Kim",
          avatar: "https://randomuser.me/api/portraits/women/12.jpg",
          rating: 4,
          date: "2024-02-03",
          title: "Very comfortable",
          comment: "Great comfort and battery life, but a bit pricey."
        },
        {
          name: "Daniel Green",
          avatar: "https://randomuser.me/api/portraits/men/13.jpg",
          rating: 5,
          date: "2024-02-20",
          title: "Best headphones I owned",
          comment: "Crystal clear audio and excellent build quality."
        }
      ]
    },

    {
      id: "p2",
      name: "Smart Fitness Watch",
      description: "Track your workouts, heart rate, sleep, and daily activity with a sleek smart watch.",
      price: 149.99,
      imageUrl: "https://images.unsplash.com/photo-1516574187841-cb9cc2ca948b",
      rating: 4.5,
      reviewCount: 621,
      inStock: true,
      category: "electronics",
      reviews: [
        {
          name: "Emily Carter",
          avatar: "https://randomuser.me/api/portraits/women/14.jpg",
          rating: 5,
          date: "2024-01-10",
          title: "Great fitness tracker",
          comment: "Tracks everything I need and battery lasts days."
        },
        {
          name: "Mark Wilson",
          avatar: "https://randomuser.me/api/portraits/men/15.jpg",
          rating: 4,
          date: "2024-02-01",
          title: "Good value",
          comment: "Works well, but screen could be brighter."
        }
      ]
    },

    {
      id: "p3",
      name: "Bluetooth Speaker",
      description: "Portable waterproof speaker with powerful sound and deep bass.",
      price: 79.99,
      imageUrl: "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1",
      rating: 4.6,
      reviewCount: 430,
      inStock: false,
      category: "electronics",
      reviews: [
        {
          name: "Chris Adams",
          avatar: "https://randomuser.me/api/portraits/men/16.jpg",
          rating: 5,
          date: "2024-01-05",
          title: "Loud and clear",
          comment: "Perfect for outdoor parties and beach trips."
        }, {
          name: "Chris Adams 1",
          avatar: "https://randomuser.me/api/portraits/men/16.jpg",
          rating: 5,
          date: "2024-01-05",
          title: "Loud and clear",
          comment: "Perfect for outdoor parties and beach trips."
        },
        {
          name: "Chris Adams2",
          avatar: "https://randomuser.me/api/portraits/men/16.jpg",
          rating: 5,
          date: "2024-01-05",
          title: "Loud and clear",
          comment: "Perfect for outdoor parties and beach trips."
        },
        {
          name: "Chris Adams3",
          avatar: "https://randomuser.me/api/portraits/men/16.jpg",
          rating: 5,
          date: "2024-01-05",
          title: "Loud and clear",
          comment: "Perfect for outdoor parties and beach trips."
        },
        {
          name: "Nina Patel",
          avatar: "https://randomuser.me/api/portraits/women/17.jpg",
          rating: 4,
          date: "2024-01-22",
          title: "Solid speaker",
          comment: "Great bass, but battery could be longer."
        },
        {
          name: "Oliver Stone",
          avatar: "https://randomuser.me/api/portraits/men/18.jpg",
          rating: 3,
          date: "2024-02-11",
          title: "Good but heavy",
          comment: "Sound is good but a bit bulky to carry."
        }
      ]
    },

    {
      id: "p4",
      name: "4K Action Camera",
      description: "Capture stunning videos and photos in extreme conditions with this rugged camera.",
      price: 229.99,
      imageUrl: "https://images.unsplash.com/photo-1484506399805-c273b8e91dce",
      rating: 4.4,
      reviewCount: 312,
      inStock: true,
      category: "electronics",
      reviews: [
        {
          name: "Ryan Scott",
          avatar: "https://randomuser.me/api/portraits/men/19.jpg",
          rating: 5,
          date: "2024-02-09",
          title: "Perfect for travel",
          comment: "Footage looks incredible even in low light."
        },
        {
          name: "Laura White",
          avatar: "https://randomuser.me/api/portraits/women/20.jpg",
          rating: 4,
          date: "2024-01-29",
          title: "Great quality",
          comment: "Very durable, just wish the battery lasted longer."
        }
      ]
    },

    {
      id: "p5",
      name: "Gaming Mouse",
      description: "High-precision gaming mouse with customizable buttons and RGB lighting.",
      price: 49.99,
      imageUrl: "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7",
      rating: 4.8,
      reviewCount: 515,
      inStock: true,
      category: "electronics",
      reviews: [
        {
          name: "Kevin Moore",
          avatar: "https://randomuser.me/api/portraits/men/21.jpg",
          rating: 5,
          date: "2024-02-15",
          title: "Super responsive",
          comment: "Perfect for FPS games, very accurate."
        },
        {
          name: "Jessica Ray",
          avatar: "https://randomuser.me/api/portraits/women/22.jpg",
          rating: 5,
          date: "2024-01-12",
          title: "Love the RGB",
          comment: "Looks amazing and feels great in hand."
        },
        {
          name: "Tom Harris",
          avatar: "https://randomuser.me/api/portraits/men/23.jpg",
          rating: 4,
          date: "2024-02-03",
          title: "Great mouse",
          comment: "Buttons are nice, software is okay."
        }
      ]
    },

    {
      id: "p6",
      name: "Classic Denim Jacket",
      description: "Stylish and durable denim jacket perfect for any season.",
      price: 89.99,
      imageUrl: "https://images.unsplash.com/photo-1543076447-215ad9ba6923",
      rating: 4.5,
      reviewCount: 210,
      inStock: true,
      category: "clothing",
      reviews: [
        {
          name: "Hannah Brooks",
          avatar: "https://randomuser.me/api/portraits/women/24.jpg",
          rating: 5,
          date: "2024-01-18",
          title: "Stylish",
          comment: "Fits perfectly and looks great with anything."
        }
      ]
    },

    {
      id: "p7",
      name: "Cotton Graphic T-Shirt",
      description: "Soft, breathable t-shirt with a modern graphic print.",
      price: 24.99,
      imageUrl: "https://images.unsplash.com/photo-1523381210434-271e8be1f52b",
      rating: 4.3,
      reviewCount: 145,
      inStock: true,
      category: "clothing",
      reviews: [
        {
          name: "Ben Turner",
          avatar: "https://randomuser.me/api/portraits/men/25.jpg",
          rating: 4,
          date: "2024-02-06",
          title: "Nice fabric",
          comment: "Comfortable and breathable, print is cool."
        },
        {
          name: "Alice Wong",
          avatar: "https://randomuser.me/api/portraits/women/26.jpg",
          rating: 3,
          date: "2024-01-25",
          title: "Runs small",
          comment: "Good quality but size runs a bit tight."
        }
      ]
    },

    {
      id: "p8",
      name: "Running Sneakers",
      description: "Lightweight sneakers designed for comfort and high-performance running.",
      price: 119.99,
      imageUrl: "https://images.unsplash.com/photo-1542291026-7eec264c27ff",
      rating: 4.7,
      reviewCount: 388,
      inStock: false,
      category: "clothing",
      reviews: [
        {
          name: "Jake Miller",
          avatar: "https://randomuser.me/api/portraits/men/27.jpg",
          rating: 5,
          date: "2024-02-10",
          title: "Very comfortable",
          comment: "Great support for long runs."
        },
        {
          name: "Sophia Reed",
          avatar: "https://randomuser.me/api/portraits/women/28.jpg",
          rating: 4,
          date: "2024-01-30",
          title: "Good shoes",
          comment: "Lightweight and stylish."
        }
      ]
    },

    {
      id: "p9",
      name: "Cozy Hoodie",
      description: "Warm fleece hoodie with a relaxed fit for everyday wear.",
      price: 54.99,
      imageUrl: "https://images.unsplash.com/photo-1579572331145-5e53b299c64e",
      rating: 4.6,
      reviewCount: 267,
      inStock: true,
      category: "clothing",
      reviews: [
        {
          name: "Lucas King",
          avatar: "https://randomuser.me/api/portraits/men/29.jpg",
          rating: 5,
          date: "2024-01-14",
          title: "Super warm",
          comment: "Perfect for winter evenings."
        }
      ]
    },

    {
      id: "p10",
      name: "Ceramic Coffee Mug Set",
      description: "Set of 4 elegant ceramic mugs perfect for coffee or tea.",
      price: 29.99,
      imageUrl: "https://images.unsplash.com/photo-1509042239860-f550ce710b93",
      rating: 4.4,
      reviewCount: 189,
      inStock: true,
      category: "home",
      reviews: [
        {
          name: "Megan Fox",
          avatar: "https://randomuser.me/api/portraits/women/30.jpg",
          rating: 5,
          date: "2024-02-02",
          title: "Beautiful mugs",
          comment: "Great quality and very elegant."
        },
        {
          name: "Peter Hall",
          avatar: "https://randomuser.me/api/portraits/men/31.jpg",
          rating: 4,
          date: "2024-01-21",
          title: "Nice set",
          comment: "Good size and feel solid."
        }
      ]
    },

    {
      id: "p11",
      name: "Aroma Essential Oil Diffuser",
      description: "Ultrasonic diffuser that fills your room with relaxing scents.",
      price: 39.99,
      imageUrl: "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
      rating: 4.6,
      reviewCount: 274,
      inStock: true,
      category: "home",
      reviews: [
        {
          name: "Linda Perez",
          avatar: "https://randomuser.me/api/portraits/women/32.jpg",
          rating: 5,
          date: "2024-02-08",
          title: "Very relaxing",
          comment: "Makes my room smell amazing."
        }
      ]
    },

    {
      id: "p12",
      name: "Modern Table Lamp",
      description: "Minimalist lamp with warm lighting for desks and nightstands.",
      price: 64.99,
      imageUrl: "https://images.unsplash.com/photo-1507473885765-e6ed057f782c",
      rating: 4.5,
      reviewCount: 132,
      inStock: false,
      category: "home",
      reviews: [
        {
          name: "Aaron Young",
          avatar: "https://randomuser.me/api/portraits/men/33.jpg",
          rating: 4,
          date: "2024-01-19",
          title: "Nice design",
          comment: "Looks great on my desk."
        }
      ]
    },

    {
      id: "p13",
      name: "Soft Throw Blanket",
      description: "Ultra-soft and warm blanket perfect for sofas and beds.",
      price: 44.99,
      imageUrl: "https://plus.unsplash.com/premium_photo-1670512215279-7bbb677d52de",
      rating: 4.8,
      reviewCount: 356,
      inStock: true,
      category: "home",
      reviews: [
        {
          name: "Rachel Wood",
          avatar: "https://randomuser.me/api/portraits/women/34.jpg",
          rating: 5,
          date: "2024-02-12",
          title: "So soft",
          comment: "Best blanket I’ve ever bought."
        },
        {
          name: "David Clark",
          avatar: "https://randomuser.me/api/portraits/men/35.jpg",
          rating: 5,
          date: "2024-01-28",
          title: "Very cozy",
          comment: "Perfect for movie nights."
        }
      ]
    }
  ]
  ,
  category: 'all',
  wishlist: [],
  cartItems: [],
  user: null,
  isLoading: false,
  order: undefined,
  isSideNavOpen: true
};

// This resulted in ✘ [ERROR] An error occurred while extracting routes. when building
// TODO: Understand why, what are the valid injection contexts: https://chatgpt.com/c/698d1a82-15e0-8325-8343-d7b5ee9b8192
// const isBrowser = inject(PLATFORM_ID) === 'browser';


export const ProductStore = signalStore(
  {providedIn: 'root'},
  withState(initialState),
  // withStorageSync({
  //   key: 'product-store',
  //   select: ({wishlist, cartItems, user, products}) => ({wishlist, cartItems, user, products}),
  // }),
  withComputed(({products, category, wishlist, cartItems}) =>
    ({
      categories: computed(() => {
        const onlyCategories = products().map((p) => p.category);
        return [{name: 'all', link: ['/products', 'all']}].concat([...new Set(onlyCategories)].map((cat) => ({
          name: cat,
          link: ['/products', cat]
        })))
      }),
      filteredProducts: computed(() =>
        category() == "all" ? products() : products().filter((p) => p.category == category())
      ),
      wishlistCounter: computed(() => wishlist().length),
      cartItemsCounter: computed(() => cartItems().reduce((count, ci) => count + ci.qty, 0))
    })),
  withMethods((
    store,
    toaster = inject(Toaster),
    seoManager = inject(SeoManager)
  ) => (
    {
      setProductSeoTags: signalMethod<Product | undefined>((product) => {
        if (!product) return;
        seoManager.updateSeoTags({
          title: product.name,
          description: product.description,
          image: product.imageUrl,
          type: 'product'
        })
      }),
      setProductListSeoTags: signalMethod<string>((category: string) => {
        const categoryName = category != 'all' ? category.charAt(0).toUpperCase() + category.slice(1) : 'All Products';
        const description = category != 'all' ? `Browse our collection of ${category} products` : 'Browse our collection of products';
        seoManager.updateSeoTags({
          title: categoryName,
          description
        })
      }),
      /**
       * @deprecated
       * Toggles the sidenav open/closed state.
       * Currently, I'm using a service for it
       * So this method is here just reference,
       * and it is safe to delete
       * @example
       * <mat-sidenav mode="side" [opened]="store.isSideNavOpen()">
       *   <button matIconButton (click)="store.toggleSideNav()">
       */

      toggleSideNav:
        () => {
          patchState(store, {isSideNavOpen: !store.isSideNavOpen()});
        },
      addReview:
        ({title, productId, rating, comment}: AddReviewParam) => {
          console.log('from add review in store')
          const updatedProducts = produce(store.products(), (draft) => {
            const selectedProduct = draft.find((p) => p.id == productId)!;
            const today = new Date();
            const formattedToday =
              today.getFullYear()
              + '-' + today.getMonth().toString().padStart(2, '0')
              + '-' + today.getDate().toString().padStart(2, '0');
            selectedProduct.reviews.push({
              title,
              comment,
              rating,
              name: store.user()!.name,
              date: formattedToday,
              avatar: 'https://randomuser.me/api/portraits/men/' + Math.floor(Math.random() * 11) + '.jpg'
            })
          })
          patchState(store, {
            products: updatedProducts
          })
          console.log('before tqoster')
          toaster.success('Review added successfully')
          console.log('after toaster')
        },
      placeOrder:
        async () => {
          patchState(store, {isLoading: true});
          await new Promise((resolve) => setTimeout(resolve, 1000));
          const order: Order = {
            id: crypto.randomUUID(),
            userId: crypto.randomUUID(),
            total: store.cartItems().reduce((
              acc, item) => acc + item.product.price * item.qty, 0),
            items: store.cartItems(),
            paymentStatus: 'success'
          }
          patchState(store, {cartItems: [], isLoading: false, order})
        },
      signOut:
        () => {
          patchState(store, {user: null});
          toaster.success("Salemu Alaykom!");
        },
      signIn:
        (user: SignInParams) => {
          patchState(store, {user: {...user, name: "default name"}});
          toaster.success("signed In!");
        },
      signUp:
        (user: User) => {
          patchState(store, {user});
          toaster.success("Account Created");
        },
      removeFromCart:
        (cartItem: CartItem) => {
          const updatedCart = store.cartItems().filter((ci) => ci.product.id != cartItem.product.id);
          patchState(store, {cartItems: updatedCart});
          toaster.success("Item Removed from Cart");
        },
      moveItemToWishlist:
        (cartItem: CartItem) => {
          const updatedCart = store.cartItems().filter((ci) => ci.product.id != cartItem.product.id);
          const updatedWishlist = produce(store.wishlist(), (draft) => {
            draft.push(cartItem.product);
          })
          patchState(store, {cartItems: updatedCart, wishlist: updatedWishlist});
          toaster.success("Item Moved to Wishlist");
        },
      decrementQty:
        (cartItem: CartItem) => {
          if (cartItem.qty == 1) {
            const updatedCart = store.cartItems().filter((ci) => ci.product.id != cartItem.product.id);
            patchState(store, {cartItems: updatedCart});
            toaster.success("Removed Product from Cart");
            return
          }
          const index = store.cartItems().findIndex((ci) => ci.product.id == cartItem.product.id);
          const updatedCart = produce(store.cartItems(), (draft) => {
            draft[index].qty -= 1;
          });
          patchState(store, {cartItems: updatedCart});
        },
      incrementQty:
        (cartItem: CartItem) => {
          const index = store.cartItems().findIndex((ci) => ci.product.id == cartItem.product.id);
          const updatedCart = produce(store.cartItems(), (draft) => {
            draft[index].qty += 1;
          });
          patchState(store, {cartItems: updatedCart});
        },
      moveWishlistToCart:
        () => {
          const updatedCart = produce(store.cartItems(), (draft) => {
            for (const wi of store.wishlist()) {
              if (!store.cartItems().find((ci) => ci.product.id == wi.id)) {
                draft.push({product: wi, qty: 1});
              }
            }
          });
          patchState(store, {cartItems: updatedCart, wishlist: []});
          toaster.success("Moved Items from Wishlist To Cart");
        },
      addToCart:
        (product: Product, quantity = 1) => {
          const index = store.cartItems().findIndex((ci) => ci.product.id == product.id);
          const updatedCart = produce(store.cartItems(), (draft) => {
              if (index == -1) {
                draft.push({product, qty: quantity});
              } else {
                draft[index].qty += quantity;
              }
            }
          );
          patchState(store, {cartItems: updatedCart});
          toaster.success(index == -1 ? "Product Added to Cart" : "Product Added Again")
        },
      setCategory:
        signalMethod<string>((cat: string) => {
          patchState(store, {category: cat})
        }),
      addToWishlist:
        (product: Product) => {
          const updatedWishlist = produce(store.wishlist(), (draft) => {
            if (!draft.find(p => p.id === product.id)) {
              draft.push(product);
            }
          });
          patchState(store, {wishlist: updatedWishlist});
          toaster.success("Product Added to Wishlist")
        },
      removeFromWishlist:
        (product: Product) => {
          patchState(store, (state) => (
            {wishlist: state.wishlist.filter((p) => p.id !== product.id)}
          ));
          toaster.success("Product Removed to Wishlist")
        },
      clearWishlist:
        () => {
          patchState(store, {wishlist: []});
          toaster.success("Wishlist Cleared!")
        }
    }
  )))
;
