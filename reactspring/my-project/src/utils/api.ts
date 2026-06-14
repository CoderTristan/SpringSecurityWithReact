const BASE_URL = 'http://localhost:8080/api/v1';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';

export const apiRequest = async <T>(
  endpoint: string,
  method: HttpMethod = 'GET',
  body: unknown = null
): Promise<T> => {
  const token = localStorage.getItem('jwt_token');

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const config: RequestInit = {
    method,
    headers,
  };

  if (body) {
    config.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(`${BASE_URL}${endpoint}`, config);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || 'An unexpected error occurred');
    }

    if (response.status === 204) {
      return {} as T;
    }

    return await response.json() as T;
  } catch (error) {
    console.error('API Service Error:', error);
    throw error;
  }
};