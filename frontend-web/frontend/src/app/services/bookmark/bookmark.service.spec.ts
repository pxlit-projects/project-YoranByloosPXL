import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BookmarkService } from './bookmark.service';
import { AuthService } from '../auth/auth.service';
import { Post } from '../../models/post.model';

describe('BookmarkService', () => {
  let service: BookmarkService;
  let http: HttpTestingController;

  const BASE = 'http://localhost:8084/bookmarks';
  const mockAuth = (username: string | null) => ({
    username$: of(username)
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth('ann') }]
    });
    service = TestBed.inject(BookmarkService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('isBookmarked() gebruikt getMy en matcht op id', async () => {
    spyOn(service as any, 'getMy').and.returnValue(Promise.resolve([
      { id: 3, title:'x', content:'', author:'', status:'GEPUBLICEERD', published:true, createdAt:'', updatedAt:'' }
    ] as Post[]));

    await expectAsync(service.isBookmarked(3)).toBeResolvedTo(true);
    await expectAsync(service.isBookmarked(99)).toBeResolvedTo(false);
  });

  it('gooit fout als niet ingelogd (headers())', async () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [{ provide: AuthService, useValue: mockAuth(null) }]
    });
    const s = TestBed.inject(BookmarkService);

    await expectAsync(s.getMy()).toBeRejectedWithError('Niet ingelogd');
  });
});
