import {Injectable} from '@angular/core';
import {DialogModalComponent} from './dialog-modal/dialog-modal.component';
import {ModalDismissReasons, NgbModal} from "@ng-bootstrap/ng-bootstrap";

@Injectable({
    providedIn: 'root'
})
export class DialogService {

    constructor(private modalService: NgbModal) {
    }

    open(message: string, messageType: string, classes: string, iconClass: string, isConfirmation?: boolean): Promise<any> {
        const dialogModal = this.modalService.open(DialogModalComponent, { centered: true });

        dialogModal.componentInstance.message = message;
        dialogModal.componentInstance.class = classes;
        dialogModal.componentInstance.messageType = messageType;
        dialogModal.componentInstance.iconClass = iconClass;
        dialogModal.componentInstance.isConfirmation = isConfirmation;

        return dialogModal.result.then((result) => {
            return result;
        }, (reason) => {
            if (reason === ModalDismissReasons.ESC) {
                setTimeout(() => {
                    window.location.reload();
                }, 500);
            }
            return false;
        });
    }
}
