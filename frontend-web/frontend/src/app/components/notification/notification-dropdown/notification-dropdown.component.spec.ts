import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';

import { NotificationDropdownComponent } from './notification-dropdown.component';
import { AuthService } from '../../../services/auth/auth.service';
import { NotificationService, Notification } from '../../../services/notification/notification.service';

describe('NotificationDropdownComponent', () => {
  let auth: { username$: any };
  let api: jasmine.SpyObj<NotificationService>;

  const LIST: Notification[] = [
    { id: 11, recipient: 'ann', message: 'ok', createdAt: '2024-01-01T10:00:00' },
    { id: 12, recipient: 'ann', message: 'hoi', createdAt: '2024-01-02T10:00:00' },
  ];

  beforeEach(async () => {
    auth = { username$: of('ann') };
    api = jasmine.createSpyObj<NotificationService>('NotificationService', [
      'getForUser', 'deleteOne'
    ]);

    await TestBed.configureTestingModule({
      imports: [NotificationDropdownComponent], 
      providers: [
        { provide: AuthService, useValue: auth },
        { provide: NotificationService, useValue: api },
      ],
    }).compileComponents();
  });

  function create() {
    const fixture = TestBed.createComponent(NotificationDropdownComponent);
    const comp = fixture.componentInstance;
    return { fixture, comp };
  }

  it('toggle: opent en laadt items bij eerste open', async () => {
    api.getForUser.and.returnValue(of([...LIST]));

    const { fixture, comp } = create();

    await comp.toggle(); 
    fixture.detectChanges();

    expect(comp.open()).toBeTrue();
    expect(api.getForUser).toHaveBeenCalledOnceWith('ann');
    expect(comp.items().length).toBe(2);

    const badge = fixture.debugElement.query(By.css('.badge')).nativeElement as HTMLElement;
    expect(badge.textContent?.trim()).toBe('2');
  });

  it('refresh: zet error wanneer API faalt', async () => {
    api.getForUser.and.returnValue(throwError(() => new Error('nope')));

    const { comp } = create();
    await comp.refresh();

    expect(comp.loading()).toBeFalse();
    expect(comp.error()).toBe('Kon meldingen niet laden');
  });

  it('removeOne: roept api en verwijdert item uit lijst', async () => {
    api.getForUser.and.returnValue(of([...LIST]));
    api.deleteOne.and.returnValue(of(void 0));

    const { fixture, comp } = create();
    await comp.toggle(); 
    fixture.detectChanges();

    await comp.removeOne(LIST[0]);
    fixture.detectChanges();

    expect(api.deleteOne).toHaveBeenCalledOnceWith(11, 'ann');
    expect(comp.items().map(i => i.id)).toEqual([12]);
  });

  it('sluit dropdown bij klik buiten .notif-root', async () => {
    api.getForUser.and.returnValue(of([...LIST]));
    const { fixture, comp } = create();

    await comp.toggle();
    expect(comp.open()).toBeTrue();

    document.body.click();

    expect(comp.open()).toBeFalse();
  });
});
