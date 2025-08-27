import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { SubmissionsComponent } from './submissions.component';
import { PostService } from '../../services/post/post.service';
import { Post } from '../../models/post.model';

describe('SubmissionsComponent', () => {
  let postSrv: jasmine.SpyObj<PostService>;

  const POSTS: Post[] = [
    {
      id: 1, title: 'Oké', content: '...', author: 'ann',
      status: 'GOEDGEKEURD', published: false,
      createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-03T00:00:00'
    },
    {
      id: 2, title: 'Nope', content: '...', author: 'ann',
      status: 'GEWEIGERD', published: false,
      createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-02T00:00:00'
    },
    {
      id: 3, title: 'Nog bezig', content: '...', author: 'ann',
      status: 'INGEDIEND', published: false,
      createdAt: '2024-01-01T00:00:00', updatedAt: '2024-01-02T12:00:00'
    },
  ];

  beforeEach(async () => {
    postSrv = jasmine.createSpyObj<PostService>('PostService', [
      'getMySubmissions', 'publish', 'toDraft'
    ]);

    await TestBed.configureTestingModule({
      imports: [SubmissionsComponent],
      providers: [{ provide: PostService, useValue: postSrv }],
    }).compileComponents();
  });

  it('groepeert inzendingen bij load()', () => {
    postSrv.getMySubmissions.and.returnValue(of(POSTS));

    const fixture = TestBed.createComponent(SubmissionsComponent);
    fixture.detectChanges(); 

    const comp = fixture.componentInstance;
    expect(postSrv.getMySubmissions).toHaveBeenCalled();
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('');

    expect(comp.grouped.accepted.length).toBe(1);
    expect(comp.grouped.rejected.length).toBe(1);
    expect(comp.grouped.submitted.length).toBe(1);
  });

  it('zet foutmelding als ophalen faalt', () => {
    postSrv.getMySubmissions.and.returnValue(throwError(() => new Error('x')));

    const fixture = TestBed.createComponent(SubmissionsComponent);
    fixture.detectChanges();

    const comp = fixture.componentInstance;
    expect(comp.loading).toBeFalse();
    expect(comp.error).toBe('Kon je inzendingen niet laden.');
  });

  it('publish(): roept service aan en refresht', async () => {
    postSrv.getMySubmissions.and.returnValue(of(POSTS));
    postSrv.publish.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(SubmissionsComponent);
    const comp = fixture.componentInstance;
    fixture.detectChanges();

    postSrv.getMySubmissions.and.returnValue(of(POSTS.filter(p => p.id !== 1)));

    await comp.publish(POSTS[0]);

    expect(postSrv.publish).toHaveBeenCalledOnceWith(1);
    expect(postSrv.getMySubmissions).toHaveBeenCalledTimes(2);
    expect(comp.error).toBe('');
  });

  it('backToDraft(): roept service aan en refresht', async () => {
    postSrv.getMySubmissions.and.returnValue(of(POSTS));
    postSrv.toDraft.and.returnValue(Promise.resolve());

    const fixture = TestBed.createComponent(SubmissionsComponent);
    const comp = fixture.componentInstance;
    fixture.detectChanges();

    postSrv.getMySubmissions.and.returnValue(of(POSTS.filter(p => p.id !== 2)));

    await comp.backToDraft(POSTS[1]);

    expect(postSrv.toDraft).toHaveBeenCalledOnceWith(2);
    expect(postSrv.getMySubmissions).toHaveBeenCalledTimes(2);
    expect(comp.error).toBe('');
  });
});
