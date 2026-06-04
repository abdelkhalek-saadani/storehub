import { Component, signal, computed, input } from '@angular/core';

@Component({
  selector: 'app-gallery',
  imports: [],
  template: `
    <div class="flex flex-col gap-1 md:gap-6 md:max-w-114">
      <div class="rounded-2xl min-w-70 h-46 md:h-152 overflow-hidden">
        <img
          [src]="selectedImageUrl()"
          [alt]="'Image ' + selectedImageId()"
          class="w-full h-full object-cover"
        />
      </div>
      <div class="flex gap-4 md:gap-5 justify-center">
        @for (image of images; track image.id) {
          <div
            class="rounded-2xl overflow-hidden aspect-square h-13 md:aspect-none md:w-19 md:h-25 cursor-pointer"
            (click)="selectImage(image.id)"
            [class.ring-2]="selectedImageId() === image.id"
            [class.ring-primary]="selectedImageId() === image.id"
            [class.opacity-100]="selectedImageId() === image.id"
            [class.opacity-50]="selectedImageId() !== image.id"
          >
            <img
              [src]="image.imgUrl"
              [alt]="'Image' + image.id"
              class="object-cover w-full h-full"
            />
          </div>
        }
      </div>
    </div>
  `,
  styles: ``,
})
export class Gallery {
  selectedImageId = signal(0);
  someinput = input();

  selectedImageUrl = computed(
    () => this.images.find((el) => el.id == this.selectedImageId())?.imgUrl,
  );

  images = [
    { id: 0, imgUrl: 'https://dummyimage.com/400x400/000000/fff&text=Image0' },
    { id: 1, imgUrl: 'https://dummyimage.com/400x400/000000/fff&text=Image1' },
    { id: 2, imgUrl: 'https://dummyimage.com/400x400/000000/fff&text=Image2' },
    { id: 3, imgUrl: 'https://dummyimage.com/400x400/000000/fff&text=Image3' },
    { id: 4, imgUrl: 'https://dummyimage.com/400x400/000000/fff&text=Image4' },
  ];

  selectImage(id: number) {
    this.selectedImageId.set(id);
  }
}
