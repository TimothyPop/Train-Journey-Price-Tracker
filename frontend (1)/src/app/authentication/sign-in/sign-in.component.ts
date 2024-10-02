import {Component} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';
import {CustomizerSettingsService} from '../../customizer-settings/customizer-settings.service';
import {ApiService} from "../../customs/_services/api.service";
import {Endpoints} from "../../customs/endpoints";
import {finalize, of, tap} from "rxjs";
import {catchError} from "rxjs/operators";
import {HttpClientModule} from "@angular/common/http";
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";
import {DialogService} from "../../customs/_modal/dialog.service";
import {environment} from "../../../environments/environment";

@Component({
    selector: 'app-sign-in',
    standalone: true,
    providers: [ApiService, Endpoints, DialogService],
    imports: [
        RouterLink,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatCheckboxModule,
        ReactiveFormsModule,
        NgIf,
        NgxSpinnerComponent,
        HttpClientModule],
    templateUrl: './sign-in.component.html',
    styleUrl: './sign-in.component.scss'
})
export class SignInComponent {

    isToggled = false;
    passHide = true;
    authForm: FormGroup;
    returnUrl: string;

    constructor(
        private fb: FormBuilder,
        private router: Router,
        public themeService: CustomizerSettingsService,
        private apiService: ApiService,
        private endpoints: Endpoints,
        private spinner: NgxSpinnerService,
        private route: ActivatedRoute,
        private dialogService: DialogService
    ) {
        this.authForm = this.fb.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required, Validators.minLength(6)]],
        });
        this.themeService.isToggled$.subscribe(isToggled => {
            this.isToggled = isToggled;
        });
    }

    onSubmit() {
        if (this.authForm.valid) {
            this.spinner.show();

            this.apiService.post(this.authForm.value, this.endpoints.login).pipe(
                tap((response: any) => {
                    const data = response.data;
                    localStorage.setItem('user_token', data.token);
                    localStorage.setItem('user_id', data.id);
                    localStorage.setItem('user_name', data.name);
                    localStorage.setItem('user_username', data.username);
                    localStorage.setItem('user_roles', data.roles);

                    let defaultUrl = '';
                    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || defaultUrl;
                    this.router.navigate([this.returnUrl]);
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
        } else {
            this.dialogService.open('Form is invalid. Please check the fields.', environment.error_message_type, 'danger', environment.error_icon);
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
