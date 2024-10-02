import {Component} from '@angular/core';
import {MatCard} from "@angular/material/card";
import {DatePipe, NgForOf, NgIf} from "@angular/common";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CustomizerSettingsService} from "../../../customizer-settings/customizer-settings.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {ApiService} from "../../_services/api.service";
import {Endpoints} from "../../endpoints";
import {DialogService} from "../../_modal/dialog.service";
import {debounceTime, distinctUntilChanged, finalize, Observable, of, switchMap, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {catchError} from "rxjs/operators";
import {environment} from "../../../../environments/environment";
import {NgxSpinnerComponent, NgxSpinnerService} from "ngx-spinner";
import {LoggerService} from "../../_services/logger.service";
import moment from "moment/moment";

@Component({
    selector: 'app-manage-journey',
    standalone: true,
    imports: [
        MatCard,
        NgForOf,
        NgIf,
        ReactiveFormsModule,
        FormsModule,
        DatePipe,
        NgxSpinnerComponent
    ],
    providers: [ApiService, Endpoints, DialogService, DatePipe],
    templateUrl: './manage-journey.component.html',
    styleUrl: './manage-journey.component.scss'
})
export class ManageJourneyComponent {
    journeyForm: FormGroup;
    queryDeparture: string = '';
    csrDep: string = '';
    queryDestination: string = '';
    csrDes: string = '';
    departureDate: string = '';
    departureTime: string = '';
    adults: number = 1;
    children: number = 0;
    stationsDep: any[] = [];
    stationsDes: any[] = [];
    searchResult: any;
    searchTime: string;

    constructor(
        public themeService: CustomizerSettingsService,
        private modalService: NgbModal,
        private spinner: NgxSpinnerService,
        private loggerService: LoggerService,
        private dialogService: DialogService,
        private apiService: ApiService,
        private endpoints: Endpoints) {

        this.journeyForm = new FormGroup({
            departure: new FormControl(''),
            destination: new FormControl(''),
            journeyType: new FormControl('single'),
            date: new FormControl('2024-08-16'),
            time: new FormControl('15:45'),
            adults: new FormControl(1),
            children: new FormControl(0),
            railcard: new FormControl('')
        });
    }

    ngOnInit() {

    }

    onSearchDep() {
        if (this.queryDeparture.length > 1) {
            this.spinner.show();
            this.apiService.get('', `${this.endpoints.settings_base_endpoint}/stationPicker/${this.queryDeparture}`).pipe(
                tap((response: any) => {
                    this.stationsDep = response;
                    console.log(this.stationsDep.length)
                }),
                catchError((error: { status: number }) => {
                    this.loggerService.log('error', error.status);
                    if (error.status === 401) {
                        this.dialogService.open('Token Expired. Redirecting to Login Page', environment.error_message_type, 'danger', environment.error_icon);
                    } else if (error.status === 500) {
                        this.dialogService.open('Internal Server Error!!!', environment.error_message_type, 'danger', environment.error_icon);
                    } else {
                        this.dialogService.open('Bad Request. Please Try Again!', environment.error_message_type, 'danger', environment.error_icon);
                    }

                    return of(null); // Handle the error appropriately
                }),
                finalize(() => {
                    this.spinner.hide(); // Hide spinner
                })
            ).subscribe();
        } else {
            this.stationsDep = []; // Clear stations if query is too short
        }
    }

    onSearchDes() {
        if (this.queryDestination.length > 1) {
            this.spinner.show();
            this.apiService.get('', `${this.endpoints.settings_base_endpoint}/stationPicker/${this.queryDestination}`).pipe(
                tap((response: any) => {
                    this.stationsDes = response;
                    console.log(this.stationsDes.length)
                }),
                catchError((error: { status: number }) => {
                    this.loggerService.log('error', error.status);
                    if (error.status === 401) {
                        this.dialogService.open('Token Expired. Redirecting to Login Page', environment.error_message_type, 'danger', environment.error_icon);
                    } else if (error.status === 500) {
                        this.dialogService.open('Internal Server Error!!!', environment.error_message_type, 'danger', environment.error_icon);
                    } else {
                        this.dialogService.open('Bad Request. Please Try Again!', environment.error_message_type, 'danger', environment.error_icon);
                    }

                    return of(null); // Handle the error appropriately
                }),
                finalize(() => {
                    this.spinner.hide(); // Hide spinner
                })
            ).subscribe();
        } else {
            this.stationsDes = []; // Clear stations if query is too short
        }
    }

    selectStationDep(station: any) {
        this.queryDeparture = station.stationName; // Set the input value to the selected station name
        this.csrDep = station.crsCode; // Set the input value to the selected station name
        this.stationsDep = []; // Clear the suggestions
    }

    selectStationDes(station: any) {
        this.queryDestination = station.stationName;
        this.csrDes = station.crsCode;
        this.stationsDes = []; // Clear the suggestions
    }

    openModal(content: any) {
        this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title', size: 'lg'}).result.then(
            (result) => {
                // Handle form submission if the modal is closed with a positive action
                if (result === 'save') {
                }
            },
            (reason) => {
                // Handle modal dismissal
            }
        );
    }

    onSubmit() {
        const requestPayload = {
            origin: {
                crs: this.csrDep,
                group: false,
            },
            destination: {
                crs: this.csrDes,
                group: false,
            },
            outwardTime: {
                travelTime: `${this.departureDate}T${this.departureTime}:00+01:00`, // Adjust timezone if needed
                type: 'DEPART',
            },
            fareRequestDetails: {
                passengers: {
                    adult: Number(this.adults),
                    child: Number(this.children),
                },
                fareClass: 'ANY',
                railcards: [],
            },
        };

        this.searchTime = `${this.departureDate}T${this.departureTime}:00+01:00`;
        this.spinner.show();
        this.apiService.post(requestPayload, environment.base_url + '/api/csv/journey-planner').pipe(
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
                this.searchResult = response;
                this.modalService.dismissAll();
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

    saveJourney(sr: any) {
        console.log(sr);
        let datePipe = new DatePipe('en-UK');
        const requestPayload = {
            name: sr.origin.name + ' - ' + sr.destination.name,
            origin: sr.origin.name,
            originCrs: sr.origin.crsCode,
            destination: sr.destination.name,
            desCrs: sr.destination.crsCode,
            type: "DEPART",
            child: sr.fares[0].individualFare.passengers.child,
            adult: sr.fares[0].individualFare.passengers.adult,
            dateTime: sr.timetable.scheduled.arrival,
            dateStart: sr.timetable.scheduled.departure,
            searchTime: this.searchTime,
            alertPrice: sr.fares[0].totalPrice / 100,
            totalLowestPrice: sr.fares[0].totalPrice / 100,
            duration: sr.duration,
            user: {
                id: localStorage?.getItem('user_id')
            },
            selectedRoutes: sr.signature
        }

        this.spinner.show();
        this.apiService.post(requestPayload, this.endpoints.journey + '/save').pipe(
            tap((response: any) => {
                console.log('Response:', response);
                this.dialogService.open('Saved Successfully', environment.info_message_type, 'success', environment.info_icon);
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

    convertTime(time: string) {
        // Use moment to format the date with timezone
        const date = moment.parseZone(time);

        // Format the date to human-friendly format
        return date.format('MMMM Do YYYY, h:mm:ss a');
    }
}
