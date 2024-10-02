import {Component} from '@angular/core';
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {FormControl, FormGroup, FormsModule} from "@angular/forms";
import {MatCard} from "@angular/material/card";
import {CustomizerSettingsService} from "../../../customizer-settings/customizer-settings.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";
import {LoggerService} from "../../_services/logger.service";
import {DialogService} from "../../_modal/dialog.service";
import {ApiService} from "../../_services/api.service";
import {Endpoints} from "../../endpoints";
import {finalize, of, tap} from "rxjs";
import {catchError} from "rxjs/operators";
import {environment} from "../../../../environments/environment";
import moment from "moment";

@Component({
    selector: 'app-my-saved-journey',
    standalone: true,
    imports: [
        DatePipe,
        FormsModule,
        MatCard,
        NgForOf,
        NgIf,
        NgClass,
        NgxSpinnerComponent
    ],
    providers: [ApiService, Endpoints, DialogService, DatePipe],
    templateUrl: './my-saved-journey.component.html',
    styleUrl: './my-saved-journey.component.scss'
})
export class MySavedJourneyComponent {
    searchResult: any;

    constructor(
        public themeService: CustomizerSettingsService,
        private modalService: NgbModal,
        private spinner: NgxSpinnerService,
        private loggerService: LoggerService,
        private dialogService: DialogService,
        private apiService: ApiService,
        private endpoints: Endpoints) {
        this.getSavedJourney();
    }

    getSavedJourney() {
        this.spinner.show();
        this.apiService.get(localStorage?.getItem('user_id'), this.endpoints.journey + '/user').pipe(
            tap((response: any) => {
                console.log('Response:', response);
                this.searchResult = response;
            }),
            catchError((error: any) => {
                console.error('Error:', error);
                this.dialogService.open('An error occurred. Please try again.', 'error', 'danger', 'error-icon');
                return of(null);
            }),
            finalize(() => {
                this.spinner.hide();
            })
        ).subscribe();
    }

    refreshPrice(sr: any) {
        const dateTimeString = sr.dateStart;
        const [date, time] = dateTimeString.split(' ');

        const requestPayload = {
            origin: {
                crs: sr.originCrs,
                group: false,
            },
            destination: {
                crs: sr.desCrs,
                group: false,
            },
            outwardTime: {
                travelTime: sr.searchTime, // Adjust timezone if needed
                type: 'DEPART',
            },
            fareRequestDetails: {
                passengers: {
                    adult: Number(sr.adult),
                    child: Number(sr.child),
                },
                fareClass: 'ANY',
                railcards: [],
            },
        };

        this.spinner.show();
        this.apiService.post(requestPayload, this.endpoints.journey + '/refresh-price/' + sr.id).pipe(
            tap((response: any) => {

                response.outwardJourneys.forEach((journey: { fares: any[]; }) => {
                    if (journey.fares && journey.fares.length > 0) {
                        // Find the minimum fare by totalPrice
                        const lowestFare = journey.fares.reduce((min, fare) => fare.totalPrice < min.totalPrice ? fare : min);

                        // Replace the fares array with only the lowest fare
                        journey.fares = [lowestFare];
                    }
                });
                console.log('Response:', response);
                sr.totalLowestPrice = response.outwardJourneys[0].fares[0].totalPrice / 100;

                this.apiService.get('', this.endpoints.journey + '/update-price/' + sr.selectedRoutes + '/' + sr.totalLowestPrice).pipe(
                    tap((response: any) => {
                        console.log('Response:', response);
                        this.dialogService.open('Price Updated Successfully', environment.info_message_type, 'success', environment.info_icon);
                    }),
                    catchError((error: any) => {
                        console.error('Error:', error);
                        this.dialogService.open('An error occurred. Please try again.', 'error', 'danger', 'error-icon');
                        return of(null);
                    }),
                    finalize(() => {
                        this.spinner.hide();
                    })
                ).subscribe();
            }),
            catchError((error: any) => {
                this.spinner.hide();
                console.error('Error:', error);
                this.dialogService.open('An error occurred. Please try again.', 'error', 'danger', 'error-icon');
                return of(null);
            }),
            finalize(() => {
                this.spinner.hide();
            })
        ).subscribe();
    }

    convertTime(time: string) {
        // Use moment to format the date with timezone
        const date = moment.parseZone(time);

        // Format the date to human-friendly format
        return date.format('MMMM Do YYYY, h:mm:ss a');
    }

    isTimeInPast(time: string): boolean {
        const date = moment.parseZone(time);
        const now = moment(); // Current time
        return date.isBefore(now); // Compare if the date is before the current time
    }
}
