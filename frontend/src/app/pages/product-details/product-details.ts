import { Component } from '@angular/core';

@Component({
  selector: 'app-product-details',
  imports: [],
  host: {
    class: 'min-h-screen flex flex-col px-4 bg-[#F8F8F8]',
  },
  template: `
    <div class="flex flex-col">
      <div class="flex justify-between items-center">
        <div>back arrow</div>
        <div>add to wishlist</div>
      </div>
      <div class="flex flex-col">
        <div>Gallery</div>
        <div>
          <div>Title</div>
          <div>Description</div>
          <div>Actions</div>
        </div>
      </div>
      <div>reviews</div>
    </div>
  `,
  styles: ``,
})
export default class ProductDetails {}
