import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {
    MAT_DIALOG_DATA,
    MatDialog,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogRef, MatDialogTitle
} from "@angular/material/dialog";
import {CommonModule} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'app-dialog-modal',
    templateUrl: './dialog-modal.component.html',
    styleUrls: ['./dialog-modal.component.css'],
    standalone: true,
    imports: [CommonModule, MatDialogContent, MatDialogActions, MatDialogClose, MatButton, MatDialogTitle],
})
export class DialogModalComponent {

    @Input() message: any;
    @Input() class: any;
    @Input() messageType: any;
    @Input() iconClass: any;
    @Input() isConfirmation: false;

    constructor(private activeModal: NgbActiveModal) {
        activeModal.close();
    }

    close() {
        this.activeModal.close();
    }

    confirm(): void {
        this.activeModal.close(true);
    }
}
