import { Component, inject, input, output, signal } from '@angular/core';
import { ViewPanel } from '../directives/view-panel';
import { MatFormField } from '@angular/material/form-field';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/select';
import { MatLabel } from '@angular/material/form-field';

@Component({
  selector: 'app-write-review',
  imports: [
    ViewPanel,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatSelect,
    MatOption,
    MatButton,
  ],
  template: `
    <div appViewPanel class="border border-gray-200 rounded-xl p-6 bg-white">
      <h2 class="text-xl font-semibold mb-6">Write a Review</h2>
      <form [formGroup]="reviewForm" (ngSubmit)="addReview()" class="flex flex-col gap-2">
        <div class="flex flex-col gap-2">
          <div class="flex flex-col gap-2 md:flex-row">
            <mat-form-field>
              <mat-label>Review Title</mat-label>
              <input
                formControlName="title"
                placeholder="Summarize your review"
                matInput=""
                type="text"
                required
              />
            </mat-form-field>

            <mat-form-field>
              <mat-select formControlName="rating" placeholder="Choose rating">
                @for (option of options(); track $index) {
                  <mat-option [value]="option.value">{{ option.label }}</mat-option>
                }
              </mat-select>
            </mat-form-field>
          </div>
          <mat-form-field class="col-span-2">
            <mat-label> Review </mat-label>
            <textarea
              placeholder="Tell others about your experience with this product"
              formControlName="comment"
              matInput
              type="text"
              rows="4"
              required
            ></textarea>
          </mat-form-field>
        </div>
        <div class="flex gap-4">
          <button matButton="filled" type="submit">Submit Review</button>
          <button matButton="outlined" type="button" (click)="canceled.emit()" class="btn-cancel">
            Cancel
          </button>
        </div>
      </form>
    </div>
  `,
  styles: ``,
})
export class WriteReview {
  fb = inject(NonNullableFormBuilder);
  reviewForm = this.fb.group({
    title: ['some title', Validators.required],
    rating: ['5', Validators.required],
    comment: ['a comment', Validators.required],
  });
  productId = input.required<string>();

  options = signal<OptionItem[]>([
    { label: '5 Stars - Excellent', value: 5 },
    { label: '4 Stars - Excellent', value: 4 },
    { label: '3 Stars - Excellent', value: 3 },
    { label: '2 Stars - Excellent', value: 2 },
    { label: '1 Stars - Excellent', value: 1 },
  ]);

  canceled = output();
  reviewAdded = output();

  addReview() {
    console.log('reached addReview');
    if (!this.reviewForm.valid) {
      this.reviewForm.markAsTouched();
      return;
    }
    // console.log('passe dvalidation')
    const { title, comment, rating } = this.reviewForm.getRawValue();
    console.log('passed the values get');

    this.reviewAdded.emit();
  }
}

type OptionItem = {
  label: string;
  value: number;
};
