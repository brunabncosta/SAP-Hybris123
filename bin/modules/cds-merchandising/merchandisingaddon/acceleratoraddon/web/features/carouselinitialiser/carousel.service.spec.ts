import { DataService } from "./carousel.service";

describe('DataService', () => {
	let service: DataService;
	let response = {
		mixcardID: 'mixcardID',
		products: null
	};

	beforeEach(() => {
		delete window.__merchcarousels;

		ACC.addons.merchandisingaddon = {};

		spyOn(DataService.prototype, 'getProducts').and.callFake(() => {
			return Promise.resolve(response) as any;
		}) // do not call network

		service = new DataService('strategy', 'https://base.url', 10);
	});

	it('service constructor should exist', () => {
		expect(DataService).toBeTruthy();
	});

	it('service instance should exist', () => {
		expect(service).toBeTruthy();
	});

	it('_buildUrl should return url', () => {
		const actual = service._buildUrl.apply({
            _buildQueryString: (a,b) => {return service._buildQueryString(a,b)},
			strategy: 'strategy',
			tenant: 'tenant',
			category: 'category',
			facets: 'facets',
            numberToDisplay: 10,
            siteId: 'main',
            language: 'en',
            productId: '1234'
		}, ['baseUrl']);

		expect(actual).toEqual('baseUrl/tenant/strategies/strategy/products?category=category&facets=facets&pageSize=10&site=main&language=en&products=1234');
	});

	it('_buildUrl should return url with encoded values', () => {
		const actual = service._buildUrl.apply({
            _buildQueryString: (a,b) => {return service._buildQueryString(a,b)},
			strategy: 'strategy',
			tenant: 'tenant',
            category: ';?category=newcategory',
            facets: '&facets=f',
            siteId: '&;main=m',
            language: ';?en',
            productId: '&1234'
		}, ['baseUrl']);

        expect(actual).toEqual('baseUrl/tenant/strategies/strategy/products?category=%3B%3Fcategory%3Dnewcategory&facets=%26facets%3Df&site=%26%3Bmain%3Dm&language=%3B%3Fen&products=%261234');
	});

	it('_getTenantId should return tenant', () => {
		ACC.addons.merchandisingaddon.hybrisTenant = '{"properties":{"hybrisTenant":"tenant"}}';
		const actual = service._getTenantId.apply(null);

		expect(actual).toEqual('tenant');
	});

	it('_getCategory should return category', () => {
		ACC.addons.merchandisingaddon.ItemCategory = '{"properties":{"ItemCategory":"category"}}';
		const actual = service._getCategory.apply(null);

		expect(actual).toEqual('category');
	});

	it('_getFacets should return facet string', () => {
		ACC.addons.merchandisingaddon.ContextFacets = '{"properties":{"ContextFacets":[{"code":"code","values":["value1","value2"]}]}}';
		const actual = service._getFacets.apply(null);

		expect(actual).toEqual('code:value1:code:value2:');
	});
});
