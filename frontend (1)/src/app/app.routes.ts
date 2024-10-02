import {Routes} from '@angular/router';
import {ProjectManagementComponent} from './dashboard/project-management/project-management.component';
import {NotFoundComponent} from './common/not-found/not-found.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {SignInComponent} from './authentication/sign-in/sign-in.component';
import {SignUpComponent} from './authentication/sign-up/sign-up.component';
import {ForgotPasswordComponent} from './authentication/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './authentication/reset-password/reset-password.component';
import {LockScreenComponent} from './authentication/lock-screen/lock-screen.component';
import {LogoutComponent} from './authentication/logout/logout.component';
import {ConfirmEmailComponent} from './authentication/confirm-email/confirm-email.component';
import {SettingsComponent} from './settings/settings.component';
import {AccountSettingsComponent} from './settings/account-settings/account-settings.component';
import {ChangePasswordComponent} from './settings/change-password/change-password.component';
import {ConnectionsComponent} from './settings/connections/connections.component';
import {PrivacyPolicyComponent} from './settings/privacy-policy/privacy-policy.component';
import {TermsConditionsComponent} from './settings/terms-conditions/terms-conditions.component';
import {authGuard} from "./customs/_guards/auth.guard";
import {MyProfileComponent} from "./my-profile/my-profile.component";
import {ManageJourneyComponent} from "./customs/_components/manage-journey/manage-journey.component";
import {MySavedJourneyComponent} from "./customs/_components/my-saved-journey/my-saved-journey.component";

export const routes: Routes = [

    // {path: 'project-management', component: ProjectManagementComponent},

    {
        path: 'authentication',
        component: AuthenticationComponent,
        children: [
            {path: '', component: SignInComponent},
            {path: 'sign-up', component: SignUpComponent},
            {path: 'forgot-password', component: ForgotPasswordComponent},
            {path: 'reset-password', component: ResetPasswordComponent},
            {path: 'lock-screen', component: LockScreenComponent},
            {path: 'confirm-email', component: ConfirmEmailComponent},
            {path: 'logout', component: LogoutComponent}
        ]
    },
    {path: 'my-profile', component: MyProfileComponent},
    {
        path: 'settings',
        component: SettingsComponent,
        children: [
            {path: '', component: AccountSettingsComponent},
            {path: 'change-password', component: ChangePasswordComponent},
            {path: 'connections', component: ConnectionsComponent},
            {path: 'privacy-policy', component: PrivacyPolicyComponent},
            {path: 'terms-conditions', component: TermsConditionsComponent}
        ]
    },
    // Custom Routes
    {
        path: '',
        component: ManageJourneyComponent,
        canActivate: [authGuard]
    },
    {
        path: 'manage-journey',
        component: ManageJourneyComponent,
        canActivate: [authGuard]
    },
    {
        path: 'saved-journey',
        component: MySavedJourneyComponent,
        canActivate: [authGuard]
    },

    {path: '**', component: NotFoundComponent} // This line will remain down from the whole pages component list
];
