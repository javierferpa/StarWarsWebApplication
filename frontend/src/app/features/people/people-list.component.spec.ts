import { TestBed } from '@angular/core/testing';
import { PeopleListComponent } from './people-list.component';
import { PeopleService } from '../../core/services/people.service';
import { of } from 'rxjs';

describe('PeopleListComponent', () => {
  let component: PeopleListComponent;
  let fixture: any;

  beforeEach(async () => {
    const mockPeopleService = {
      list: jasmine.createSpy('list').and.returnValue(of({
        page: 0,
        size: 15,
        total: 0,
        items: []
      }))
    };

    await TestBed.configureTestingModule({
      imports: [PeopleListComponent],
      providers: [
        { provide: PeopleService, useValue: mockPeopleService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(PeopleListComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
