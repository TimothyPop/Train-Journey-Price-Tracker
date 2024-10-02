import {environment} from '../../environments/environment';

export class Endpoints {
    private baseUrl = environment.base_url;

    // Authentication
    public login = this.baseUrl + '/api/auth/signin';
    public signup = this.baseUrl + '/api/auth/signup';

    // Manage Samplers
    public sampler_base_endpoint = this.baseUrl + '/api/users';
    public client_base_endpoint = this.baseUrl + '/api/clients';
    public client_poc_base_endpoint = this.baseUrl + '/api/pocs';
    public contract_base_endpoint = this.baseUrl + '/api/service-contract';
    public settings_base_endpoint = this.baseUrl + '/api/csv';
    public journey = this.baseUrl + '/api/journeys';
}
