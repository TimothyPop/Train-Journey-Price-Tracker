import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageJourneyComponent } from './manage-journey.component';

describe('ManageJourneyComponent', () => {
  let component: ManageJourneyComponent;
  let fixture: ComponentFixture<ManageJourneyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageJourneyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageJourneyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
