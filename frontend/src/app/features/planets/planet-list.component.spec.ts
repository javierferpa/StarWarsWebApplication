import { TestBed } from '@angular/core/testing';
import { PlanetListComponent } from './planet-list.component';
import { PlanetsService } from '../../core/services/planets.service';
import { of } from 'rxjs';

describe('PlanetListComponent', () => {
  let component: PlanetListComponent;
  let fixture: any;

  beforeEach(async () => {
    const mockPlanetsService = {
      list: jasmine.createSpy('list').and.returnValue(of({
        page: 0,
        size: 15,
        total: 0,
        items: []
      }))
    };

    await TestBed.configureTestingModule({
      imports: [PlanetListComponent],
      providers: [
        { provide: PlanetsService, useValue: mockPlanetsService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PlanetListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
