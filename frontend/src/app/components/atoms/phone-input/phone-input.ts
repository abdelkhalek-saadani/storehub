import { Component } from '@angular/core';
import IntlTelInput from "@intl-tel-input/angular";
import "intl-tel-input/styles";

@Component({
  selector: 'app-phone-input',
  imports: [IntlTelInput],
  template: `
    <intl-tel-input
      [inputAttributes]="{  'class':'h-[52px] ' +
                                    'rounded-lg ' +
                                     'text-base ' +
                                      'border ' +
                                       'border-gray-300 ' +
                                        'focus:border-blue-500 ' +
                                         'focus:border-2 ' +
                                          'focus:outline-none '}"
      class="mt-6 w-full block flex items-center justify-center"
      initialCountry="tn"
      [loadUtils]="loadUtils"></intl-tel-input>
  `,
  styles: ``,
})
export class PhoneInput {
  loadUtils = () => import("intl-tel-input/utils");
}
