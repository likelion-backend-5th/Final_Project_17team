export class User {
    constructor(token) {
        this.token = token;
        this.headers = {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    }

    async logout(url) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: this.headers,
            });

            if (response.status === 200) {
                localStorage.removeItem('token');
                window.location.replace('/main');
            } else {
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
    async updateUser(url, FormData) {
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: this.headers,
                body: JSON.stringify(FormData)
                ,
            });

            if (response.status === 200) {
                window.location.replace('/users/profiles');
            } else {
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
    async deleteUser(url) {
        try {
            console.log(this.token)
            const response = await fetch(url, {
                method: 'DELETE',
                headers: this.headers,
            });

            if (response.status === 200) {
                window.location.replace('/main');
                console.dir(response);
            } else {
                console.error('Authentication failed: Token may be invalid or expired.');
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
}