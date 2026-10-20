export interface User {
  name: string;
  email: string;
  password: string;
}

export interface SignUpParams {
  name: string;
  email: string;
  password: string;
}
export type SignInParams = Omit<SignUpParams, 'name'>;
