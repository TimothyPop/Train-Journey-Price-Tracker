import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MySavedJourneyComponent } from './my-saved-journey.component';

describe('MySavedJourneyComponent', () => {
  let component: MySavedJourneyComponent;
  let fixture: ComponentFixture<MySavedJourneyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MySavedJourneyComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MySavedJourneyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
