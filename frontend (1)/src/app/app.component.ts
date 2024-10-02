declare let $: any;
import {Component, OnInit, Renderer2} from '@angular/core';
import {filter} from 'rxjs/operators';
import {HeaderComponent} from './common/header/header.component';
import {FooterComponent} from './common/footer/footer.component';
import {ToggleService} from './common/sidebar/toggle.service';
import {SidebarComponent} from './common/sidebar/sidebar.component';
import {CommonModule, Location, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {CustomizerSettingsComponent} from './customizer-settings/customizer-settings.component';
import {RouterOutlet, Router, NavigationCancel, NavigationEnd, RouterLink} from '@angular/router';
import {CustomizerSettingsService} from './customizer-settings/customizer-settings.service';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, RouterOutlet, SidebarComponent, HeaderComponent, FooterComponent, RouterLink, CustomizerSettingsComponent],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss',
    providers: [
        Location, {
            provide: LocationStrategy,
            useClass: PathLocationStrategy
        }
    ]
})
export class AppComponent implements OnInit {

    title = 'Daxa - Angular 17 Material Design Admin Dashboard Template';
    routerSubscription: any;
    location: any;

    // isSidebarToggled
    isSidebarToggled = false;

    // isToggled
    isToggled = false;

    private currentElement: HTMLElement | null = null;

    constructor(
        public router: Router,
        private toggleService: ToggleService,
        public themeService: CustomizerSettingsService,
        private renderer: Renderer2
    ) {
        this.toggleService.isSidebarToggled$.subscribe(isSidebarToggled => {
            this.isSidebarToggled = isSidebarToggled;
        });
        this.themeService.isToggled$.subscribe(isToggled => {
            this.isToggled = isToggled;
        });
    }

    // ngOnInit
    ngOnInit() {
        this.recallJsFuntions();
        window.addEventListener('message', this.handleMessage, false);
    }

    ngOnDestroy() {
        window.removeEventListener('message', this.handleMessage, false);
        this.removeEventListeners();
    }

    // recallJsFuntions
    recallJsFuntions() {
        this.routerSubscription = this.router.events
            .pipe(filter(event => event instanceof NavigationEnd || event instanceof NavigationCancel))
            .subscribe(event => {
                this.location = this.router.url;
                if (!(event instanceof NavigationEnd)) {
                    return;
                }
                window.scrollTo(0, 0);
            });
    }

    // Dark Mode
    toggleTheme() {
        this.themeService.toggleTheme();
    }

    // Sidebar Dark
    toggleSidebarTheme() {
        this.themeService.toggleSidebarTheme();
    }

    // Right Sidebar
    toggleRightSidebarTheme() {
        this.themeService.toggleRightSidebarTheme();
    }

    // Hide Sidebar
    toggleHideSidebarTheme() {
        this.themeService.toggleHideSidebarTheme();
    }

    // Header Dark Mode
    toggleHeaderTheme() {
        this.themeService.toggleHeaderTheme();
    }

    // Card Border
    toggleCardBorderTheme() {
        this.themeService.toggleCardBorderTheme();
    }

    // Card Border Radius
    toggleCardBorderRadiusTheme() {
        this.themeService.toggleCardBorderRadiusTheme();
    }

    // RTL Mode
    toggleRTLEnabledTheme() {
        this.themeService.toggleRTLEnabledTheme();
    }

    handleMessage = (event: MessageEvent) => {
        if (event.data.action === 'enableSelection') {
            this.addEventListeners();
        } else if (event.data.action === 'disableSelection') {
            this.removeEventListeners();
            if (this.currentElement) {
                this.renderer.setStyle(this.currentElement, 'outline', '');
                this.currentElement = null;
            }
        }
    }

    addEventListeners() {
        document.addEventListener('mousemove', this.handleMouseMove);
        document.addEventListener('contextmenu', this.handleElementClick);
    }

    removeEventListeners() {
        document.removeEventListener('mousemove', this.handleMouseMove);
        document.removeEventListener('contextmenu', this.handleElementClick);
    }

    handleMouseMove = (event: MouseEvent) => {
        const element = document.elementFromPoint(event.clientX, event.clientY) as HTMLElement;
        if (element !== this.currentElement) {
            if (this.currentElement) {
                this.renderer.setStyle(this.currentElement, 'outline', '');
            }
            this.currentElement = element;
            this.renderer.setStyle(this.currentElement, 'outline', '2px solid red');
        }
    }

    handleElementClick = (event: MouseEvent) => {
        event.preventDefault();
        event.stopPropagation();
        if (this.currentElement) {
            const elementDetails = {
                tagName: this.currentElement.tagName,
                attributes: Array.from(this.currentElement.attributes).map(attr => ({
                    name: attr.name,
                    value: attr.value
                })),
                innerHTML: this.currentElement.innerHTML
            };
            //console.log('Selected element details:', elementDetails);
            window.parent.postMessage(elementDetails, '*');
            this.renderer.setStyle(this.currentElement, 'outline', '');
            this.currentElement = null;
            this.removeEventListeners();
        }
    }
}
