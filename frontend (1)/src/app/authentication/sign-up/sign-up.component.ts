import {Component} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {RouterLink} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {CustomizerSettingsService} from '../../customizer-settings/customizer-settings.service';
import {finalize, of, tap} from "rxjs";
import {catchError} from "rxjs/operators";
import {environment} from "../../../environments/environment";
import {ApiService} from "../../customs/_services/api.service";
import {Endpoints} from "../../customs/endpoints";
import {NgxSpinnerService} from "ngx-spinner";
import {DialogService} from "../../customs/_modal/dialog.service";

@Component({
    selector: 'app-sign-up',
    standalone: true,
    providers: [ApiService, Endpoints, DialogService],
    imports: [RouterLink, MatFormFieldModule, MatInputModule, MatButtonModule, MatCheckboxModule, ReactiveFormsModule, NgIf],
    templateUrl: './sign-up.component.html',
    styleUrl: './sign-up.component.scss'
})
export class SignUpComponent {

    // isToggled
    isToggled = false;

    constructor(
        private fb: FormBuilder,
        private router: Router,
        public themeService: CustomizerSettingsService,
        private apiService: ApiService,
        private endpoints: Endpoints,
        private spinner: NgxSpinnerService,
        private dialogService: DialogService
    ) {
        this.authForm = this.fb.group({
            name: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            username: ['', [Validators.required]],
            password: ['', [Validators.required, Validators.minLength(8)]],
            phone: ['01928262662'],
        });
        this.themeService.isToggled$.subscribe(isToggled => {
            this.isToggled = isToggled;
        });
    }

    // Password Hide
    hide = true;

    // Form
    authForm: FormGroup;

    onSubmit() {
        if (this.authForm.valid) {
            this.spinner.show();

            this.apiService.post(this.authForm.value, this.endpoints.signup).pipe(
                tap((response: any) => {
                    this.router.navigate(['/']);
                }),
                catchError((error: { status: number }) => {
                    // this.loggerService.log('error', error.status);
                    if (error.status === 401) {
                        this.dialogService.open('Invalid Credentials', environment.error_message_type, 'danger', environment.error_icon);
                    } else {
                        this.dialogService.open('Internal Server Error!!!', environment.error_message_type, 'danger', environment.error_icon);
                    }
                    return of(null);  // Handle the error appropriately
                }),
                finalize(() => {
                    this.spinner.hide();
                })
            ).subscribe();
            this.router.navigate(['/']);
        } else {
            console.log('Form is invalid. Please check the fields.');
        }
    }

    // Dark Mode
    toggleTheme() {
        this.themeService.toggleTheme();
    }

    // Card Border
    toggleCardBorderTheme() {
        this.themeService.toggleCardBorderTheme();
    }

    // RTL Mode
    toggleRTLEnabledTheme() {
        this.themeService.toggleRTLEnabledTheme();
    }

}
